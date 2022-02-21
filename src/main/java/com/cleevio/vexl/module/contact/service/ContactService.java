package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.FacebookUser;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
import com.cleevio.vexl.module.contact.dto.response.FacebookContactResponse;
import com.cleevio.vexl.module.contact.dto.response.NewContactsResponse;
import com.cleevio.vexl.module.contact.dto.response.UserContactResponse;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ContactService {

    private static final String SHA256 = "SHA-256";

    private final ContactRepository contactRepository;
    private final FacebookService facebookService;

    @Transactional(readOnly = true)
    public UserContactResponse retrieveUserContactsByUser(User user) {
        log.info("Retrieving contacts for user {}",
                user.getId());

        Set<byte[]> contactsPublicKey = contactRepository.findAllContactsByPublicKey(user.getPublicKey());

        return new UserContactResponse(
                contactsPublicKey.stream()
                        .map(EncryptionUtils::encodeToBase64String)
                        .collect(Collectors.toSet())
        );
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
            throws FacebookException, NoSuchAlgorithmException {
        log.info("Checking for new Facebook connections for user {}",
                user.getId());

        FacebookUser facebookUser = this.facebookService.retrieveContacts(facebookId, accessToken);
        List<String> facebookIds = facebookUser.getFriends().stream()
                .map(FacebookUser::getId).toList();

        for (String id :
                facebookIds) {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), EncryptionUtils.createHash(id, SHA256))) {
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
    public NewContactsResponse retrieveNewContacts(User user, NewContactsRequest contactsRequest)
            throws NoSuchAlgorithmException {
        List<String> newContacts = new ArrayList<>();

        for (String contact :
                contactsRequest.getContacts()) {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), EncryptionUtils.createHash(contact, SHA256))) {
                newContacts.add(contact);
            }
        }

        return new NewContactsResponse(newContacts);
    }
}
