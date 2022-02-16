package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.response.UserContactResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

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
}
