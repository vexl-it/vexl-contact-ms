package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.request.CommonContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.ContactsImportedEvent;
import com.cleevio.vexl.module.contact.dto.response.CommonContactsResponse;
import com.cleevio.vexl.module.contact.constant.ConnectionLevel;
import com.cleevio.vexl.module.contact.event.GroupJoinedEvent;
import com.cleevio.vexl.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of receiving and deleting contacts. Adding (importing) contacts is done in ImportService.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final VContactRepository vContactRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public Page<String> retrieveContactsByUser(final User user, final int page, final int limit, final ConnectionLevel level) {
        log.info("Retrieving contacts for user {}",
                user.getId());

        return vContactRepository.findPublicKeysByMyPublicKeyAndLevel(
                user.getPublicKey(),
                ConnectionLevel.ALL == level ? EnumSet.complementOf(EnumSet.of(ConnectionLevel.ALL)) : EnumSet.of(level),
                PageRequest.of(page, limit));
    }

    public void deleteAllContacts(String userPublicKey) {
        this.contactRepository.deleteAllByPublicKey(userPublicKey);
    }

    public void deleteContacts(User user, @Valid DeleteContactsRequest deleteContactsRequest) {
        log.info("Deleting contacts for user {}",
                user.getId());

        final List<String> contactsToDelete = deleteContactsRequest.contactsToDelete().stream()
                .map(String::trim)
                .toList();

        this.contactRepository.deleteContactsByHashes(user.getHash(), contactsToDelete);
    }

    public void deleteContactByHash(String hash, String contactHash) {
        this.contactRepository.deleteContactByHash(hash, contactHash);
    }

    @Transactional(readOnly = true)
    public List<String> retrieveNewContacts(User user, @Valid NewContactsRequest contactsRequest) {
        if (contactsRequest.contacts().isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> newContacts = new ArrayList<>();

        final List<String> trimContacts = contactsRequest.contacts()
                .stream()
                .map(String::trim)
                .toList();

        final Set<String> existingContacts = this.contactRepository.retrieveExistingContacts(user.getHash(), trimContacts);

        trimContacts.forEach(tr -> {
            if (existingContacts.add(tr)) {
                newContacts.add(tr);
            }
        });

        return newContacts;
    }

    @Transactional(readOnly = true)
    public Set<String> retrieveExistingContacts(String hashFrom, List<String> trimContactsHashTo) {
        return this.contactRepository.retrieveExistingContacts(hashFrom, trimContactsHashTo);
    }

    @Transactional(readOnly = true)
    public int getContactsCountByHashFrom(final String hash) {
        return this.contactRepository.countContactsByHashFrom(hash);
    }

    @Transactional(readOnly = true)
    public int getContactsCountByHashTo(final String hash) {
        return this.contactRepository.countContactsByHashTo(hash);
    }

    @Transactional(readOnly = true)
    public CommonContactsResponse retrieveCommonContacts(final String ownerPublicKey, @Valid final CommonContactsRequest request) {
        final List<String> publicKeys = request.publicKeys();
        List<CommonContactsResponse.Contacts> contacts = new ArrayList<>();
        publicKeys.stream()
                .map(String::trim)
                .filter(pk -> !pk.equals(ownerPublicKey))
                .forEach(pk -> {
                    final List<String> commonContacts = this.contactRepository.retrieveCommonContacts(ownerPublicKey, pk);
                    contacts.add(new CommonContactsResponse.Contacts(pk, new CommonContactsResponse.Contacts.CommonContacts(commonContacts)));
                });
        return new CommonContactsResponse(contacts);
    }

    @Transactional(readOnly = true)
    public List<String> getGroupsUuidsByHash(String hash) {
        return this.contactRepository.getGroupsUuidsByHash(hash);
    }

    @Transactional(readOnly = true)
    public List<String> retrieveNewGroupMembers(final String groupUuidHash, final List<String> publicKeys) {
        return publicKeys.isEmpty() ?
                Collections.emptyList() :
                this.contactRepository.retrieveNewGroupMembers(groupUuidHash, publicKeys);
    }

    @Transactional(readOnly = true)
    public void storeNotificationsForLaterProcessing(final String groupUuid, final String publicKey) {
        final Set<String> membersFirebaseTokens = this.contactRepository.retrieveGroupMembersFirebaseTokens(groupUuid, publicKey);
        if (membersFirebaseTokens.isEmpty()) return;
        applicationEventPublisher.publishEvent(new GroupJoinedEvent(groupUuid, membersFirebaseTokens));
    }

    /**
     * Send a notification to all existing contacts, so they can encrypt their Offers for a new user.
     */
    @Async
    @Transactional(readOnly = true)
    public void sendNotificationToContacts(final Set<String> importedHashes, final User user) {
        if (importedHashes.isEmpty()) return;
        final Set<String> firebaseTokens = this.contactRepository.retrieveFirebaseTokensByHashes(importedHashes, user.getHash());
        final Set<String> firebaseTokensSecondDegrees = this.contactRepository.retrieveSecondDegreeFirebaseTokensByHashes(importedHashes, user.getHash(), firebaseTokens, ConnectionLevel.FIRST);
        if (firebaseTokens.isEmpty()) return;
        applicationEventPublisher.publishEvent(new ContactsImportedEvent(firebaseTokens, firebaseTokensSecondDegrees, user.getPublicKey()));
    }

    @Transactional(readOnly = true)
    public List<String> retrieveRemovedGroupMembers(String groupUuid, List<String> publicKeys) {
        final List<String> activeMembersPublicKeys = this.contactRepository.retrieveAllGroupMembers(groupUuid);
        publicKeys.removeAll(activeMembersPublicKeys);
        return publicKeys;
    }
}
