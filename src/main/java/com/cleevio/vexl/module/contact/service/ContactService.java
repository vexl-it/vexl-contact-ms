package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.facebook.dto.FacebookUser;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
import com.cleevio.vexl.module.facebook.dto.response.FacebookContactResponse;
import com.cleevio.vexl.module.contact.dto.response.NewContactsResponse;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.cleevio.vexl.module.facebook.service.FacebookService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ContactService {

    @Value("${hmac.secret.key}")
    private final String secretKey;

    private final ContactRepository contactRepository;
    private final FacebookService facebookService;

    @Transactional(readOnly = true)
    public Page<byte[]> retrieveUserContactsByUser(User user, int page, int limit) {
        log.info("Retrieving contacts for user {}",
                user.getId());

        return contactRepository.findAllContactsByPublicKey(user.getPublicKey(), PageRequest.of(page, limit));
    }

    public void deleteAllContacts(byte[] userPublicKey) {
        this.contactRepository.deleteAllByPublicKey(userPublicKey);
    }

    public void deleteContacts(User user, DeleteContactsRequest deleteContactsRequest) {
        log.info("Deleting contacts for user {}",
                user.getId());

        List<byte[]> contactsByteList = deleteContactsRequest.getContactsToDelete().stream()
                .map(EncryptionUtils::decodeBase64String)
                .collect(Collectors.toList());

        this.contactRepository.deleteContacts(user.getHash(), contactsByteList);
    }

    @Transactional(readOnly = true)
    public FacebookContactResponse retrieveFacebookNewContacts(User user, String facebookId, String accessToken)
            throws FacebookException {
        log.info("Checking for new Facebook connections for user {}",
                user.getId());

        FacebookUser facebookUser = this.facebookService.retrieveContacts(facebookId, accessToken);
        List<String> facebookIds = facebookUser.getFriends().stream()
                .map(FacebookUser::getId).toList();

        for (String id :
                facebookIds) {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), calculateHmacSha256(id))) {
                for (FacebookUser fu :
                        facebookUser.getFriends()) {
                    if (id.equals(fu.getId())) {
                        facebookUser.addNewFriends(fu);
                    }
                }
            }
        }

        log.info("Found {} new Facebook contacts",
                facebookUser.getNewFriends().size());

        return new FacebookContactResponse(facebookUser);

    }

    @Transactional(readOnly = true)
    public NewContactsResponse retrieveNewContacts(User user, NewContactsRequest contactsRequest) {
        List<String> newContacts = new ArrayList<>();

        for (String contact :
                contactsRequest.getContacts()) {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), calculateHmacSha256(contact))) {
                newContacts.add(contact);
            }
        }

        return new NewContactsResponse(newContacts);
    }

    private byte[] calculateHmacSha256(String value) {
        return EncryptionUtils.calculateHmacSha256(
                this.secretKey.getBytes(StandardCharsets.UTF_8),
                value.getBytes(StandardCharsets.UTF_8)
        );
    }
}
