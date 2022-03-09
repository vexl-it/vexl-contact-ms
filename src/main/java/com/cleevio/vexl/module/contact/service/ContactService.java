package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
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

/**
 * Implementation of receiving and deleting contacts. Adding (importing) contacts is done in ImportService.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ContactService {

    @Value("${hmac.secret.key}")
    private final String secretKey;

    private final ContactRepository contactRepository;

    /**
     * Retrieve public keys of all my connections (friends)
     *
     * @param user
     * @param page
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public Page<byte[]> retrieveContactsByUser(User user, int page, int limit) {
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
    public List<String> retrieveNewContacts(User user, NewContactsRequest contactsRequest) {
        List<String> newContacts = new ArrayList<>();

        contactsRequest.getContacts().forEach(c -> {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), calculateHmacSha256(c))) {
                newContacts.add(c);
            }
        });

        return newContacts;
    }

    public boolean existsByHashFromAndHashTo(byte[] hashFrom, byte[] hashTo) {
        return this.contactRepository.existsByHashFromAndHashTo(hashFrom, hashTo);
    }

    public boolean existsByHashFromAndHashTo(byte[] hashFrom, String hashToString) {
        return existsByHashFromAndHashTo(hashFrom, calculateHmacSha256(hashToString));
    }


    private byte[] calculateHmacSha256(String value) {
        return EncryptionUtils.calculateHmacSha256(
                this.secretKey.getBytes(StandardCharsets.UTF_8),
                value.getBytes(StandardCharsets.UTF_8)
        );
    }
}
