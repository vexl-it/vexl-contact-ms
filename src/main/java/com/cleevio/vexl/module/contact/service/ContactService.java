package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
import com.cleevio.vexl.module.contact.dto.response.CommonContactsResponse;
import com.cleevio.vexl.module.contact.enums.ConnectionLevel;
import com.cleevio.vexl.module.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of receiving and deleting contacts. Adding (importing) contacts is done in ImportService.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final VContactRepository vContactRepository;

    /**
     * Retrieve public keys of my connections (friends)
     *
     * @param user
     * @param page
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public Page<String> retrieveContactsByUser(User user, int page, int limit, ConnectionLevel level) {
        log.info("Retrieving contacts for user {}",
                user.getId());

        return vContactRepository.findPublicKeysByMyPublicKeyAndLevel(
                user.getPublicKey(),
                ConnectionLevel.ALL.equals(level) ? List.of(ConnectionLevel.FIRST, ConnectionLevel.SECOND) : Collections.singletonList(level),
                PageRequest.of(page, limit));
    }

    public void deleteAllContacts(String userPublicKey) {
        this.contactRepository.deleteAllByPublicKey(userPublicKey);
    }

    public void deleteContacts(User user, DeleteContactsRequest deleteContactsRequest) {
        log.info("Deleting contacts for user {}",
                user.getId());

        List<String> contactsToDelete = deleteContactsRequest.contactsToDelete().stream()
                .map(String::trim)
                .collect(Collectors.toList());

        this.contactRepository.deleteContacts(user.getHash(), contactsToDelete);
    }

    public void deleteContactByHash(String hash, String contactHash) {
        this.contactRepository.deleteContactByHash(hash, contactHash);
    }

    @Transactional(readOnly = true)
    public List<String> retrieveNewContacts(User user, NewContactsRequest contactsRequest) {
        List<String> newContacts = new ArrayList<>();

        contactsRequest.contacts().forEach(c -> {
            if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), c.trim())) {
                newContacts.add(c);
            }
        });

        return newContacts;
    }

    public boolean existsByHashFromAndHashTo(String hashFrom, String hashTo) {
        return this.contactRepository.existsByHashFromAndHashTo(hashFrom, hashTo);
    }

    @Transactional(readOnly = true)
    public int getContactsCount(String hash) {
        return this.contactRepository.countContactsByHash(hash);
    }

    @Transactional(readOnly = true)
    public CommonContactsResponse retrieveCommonContacts(String ownerPublicKey, List<String> publicKeys) {
        List<CommonContactsResponse.Contacts> contacts = new ArrayList<>();
        publicKeys.stream()
                .map(String::trim)
                .forEach(pk -> {
                    List<String> commonContacts = this.contactRepository.retrieveCommonContacts(ownerPublicKey, pk);
                    contacts.add(new CommonContactsResponse.Contacts(pk, new CommonContactsResponse.Contacts.CommonContacts(commonContacts)));
                });
        return new CommonContactsResponse(contacts);
    }

    @Transactional(readOnly = true)
    public List<String> getGroups(String hash, Set<String> groupUuidHashes) {
        return this.contactRepository.getGroups(hash, groupUuidHashes);
    }

    @Transactional(readOnly = true)
    public List<String> retrieveNewGroupMembers(final String groupUuidHash, final List<String> publicKeys) {
        return this.contactRepository.retrieveNewGroupMembers(groupUuidHash, publicKeys);
    }
}
