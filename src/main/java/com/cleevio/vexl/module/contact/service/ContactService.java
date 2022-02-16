package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.FacebookUser;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.FacebookContactRequest;
import com.cleevio.vexl.module.contact.dto.response.FacebookContactResponse;
import com.cleevio.vexl.module.contact.dto.response.UserContactResponse;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
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

    public UserContactResponse retrieveUserContactsByUser(User user) {
        log.info("Retrieving {} user contacts",
                EncryptionUtils.encodeToBase64String(user.getPublicKey())
        );

        Set<byte[]> contacts = contactRepository.findAllContactsByPublicKey(user.getPublicKey());

        return new UserContactResponse(contacts);
    }

    public void deleteAllContacts(byte[] userPublicKey) {
        this.contactRepository.deleteAllByPublicKey(userPublicKey);
    }

    public void deleteContacts(User user, DeleteContactsRequest deleteContactsRequest) {
        List<byte[]> contactsByteList = deleteContactsRequest.getContactsToDelete().stream()
                .map(EncryptionUtils::decodeBase64String)
                .collect(Collectors.toList());

        this.contactRepository.deleteContacts(user.getHash(), contactsByteList);
    }

    public FacebookContactResponse retrieveFacebookNewContacts(User user, FacebookContactRequest contactRequest)
            throws FacebookException, NoSuchAlgorithmException {
        FacebookUser facebookUser = this.facebookService.retrieveContacts(contactRequest);
        List<String> facebookIds = facebookUser.getFriends().stream()
                .map(FacebookUser::getId).toList();

        for (String facebookId :
                facebookIds) {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), EncryptionUtils.createHash(facebookId, SHA256))) {
                for (FacebookUser fu :
                        facebookUser.getFriends()) {
                    if (fu.getId() == facebookId) {
                        facebookUser.addNewFriends(fu);
                    }
                }
            }
        }

        return new FacebookContactResponse(facebookUser);

    }
}
