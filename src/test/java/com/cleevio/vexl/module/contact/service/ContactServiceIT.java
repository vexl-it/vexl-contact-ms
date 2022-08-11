package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.contact.dto.response.ImportResponse;
import com.cleevio.vexl.module.contact.constant.ConnectionLevel;
import com.cleevio.vexl.module.user.dto.request.CreateUserRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.util.CreateRequestTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactServiceIT {

    private final static String PUBLIC_KEY_USER_1 = "dummy_public_key_1";

    private final static String PUBLIC_KEY_USER_2 = "dummy_public_key_2";
    private final static String PUBLIC_KEY_USER_3 = "dummy_public_key_3";
    private final static String PUBLIC_KEY_USER_4 = "dummy_public_key_4";
    private final static String PUBLIC_KEY_USER_5 = "dummy_public_key_5";
    private final static String HASH_USER = "dummy_hash";
    private final static String PHONE_1 = "dummy_phone_1";
    private final static String PHONE_2 = "dummy_phone_2";
    private final static String PHONE_3 = "dummy_phone_3";
    private final static String PHONE_4 = "dummy_phone_4";
    private final static String FIREBASE_TOKEN_1 = "dummy_firebase_token_1";
    private final static String FIREBASE_TOKEN_2 = "dummy_firebase_token_2";
    private final static String FIREBASE_TOKEN_3 = "dummy_firebase_token_3";
    private final static String FIREBASE_TOKEN_4 = "dummy_firebase_token_4";
    private final static String GROUP_UUID = "dummy_group_uuid";
    private final static List<String> CONTACTS_1 = List.of(PHONE_1, PHONE_2);
    private final static List<String> CONTACTS_2 = List.of(PHONE_3, PHONE_4);
    private final ContactService contactService;
    private final ImportService importService;
    private final UserService userService;
    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceIT(ContactService contactService, ImportService importService,
                            UserService userService, ContactRepository contactRepository) {
        this.contactService = contactService;
        this.importService = importService;
        this.userService = userService;
        this.contactRepository = contactRepository;
    }

    @Test
    void testImportContacts_shouldBeImported() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));
        final ImportResponse importResponse = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));

        assertThat(importResponse.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);

        assertThat(contactsCount).isEqualTo(CONTACTS_1.size());

        final List<String> contacts = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();

        assertThat(contacts).hasSize(CONTACTS_1.size());
        assertThat(contacts.get(0)).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(contacts.get(1)).isEqualTo(PUBLIC_KEY_USER_3);
    }

    @Test
    void testImportContacts_sendingMyOwnNumber_shouldBeImportedWithoutMyNumber() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));

        final List<String> contactsToImport = Arrays.asList(CONTACTS_1.get(0), CONTACTS_1.get(1), HASH_USER);
        final ImportResponse importResponse = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(contactsToImport));

        assertThat(importResponse.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);

        assertThat(contactsCount).isEqualTo(CONTACTS_1.size());

        final List<String> contacts = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();

        assertThat(contacts).hasSize(CONTACTS_1.size());
        assertThat(contacts.get(0)).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(contacts.get(1)).isEqualTo(PUBLIC_KEY_USER_3);
    }

    @Test
    void testImportEmptyContacts_shouldReturnException() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);

        assertThrows(
                ConstraintViolationException.class,
                () -> importService.importContacts(user, CreateRequestTestUtil.createImportRequest(Collections.emptyList()))
        );
    }

    @Test
    void testImportSameContactsForTimes_shouldBeImportedAndNotDuplicated() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));
        final ImportResponse importResponse = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        final ImportResponse importResponse1 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        final ImportResponse importResponse2 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        final ImportResponse importResponse3 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));

        assertThat(importResponse.imported()).isTrue();
        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();
        assertThat(importResponse3.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);

        assertThat(contactsCount).isEqualTo(CONTACTS_1.size());

        final List<String> contacts = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();

        assertThat(contacts).hasSize(CONTACTS_1.size());
        assertThat(contacts.get(0)).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(contacts.get(1)).isEqualTo(PUBLIC_KEY_USER_3);
    }

    @Test
    void testRetrieveAllContacts_shouldRetrieveAllContacts() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        // first level friends
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        final User userFriend = this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));

        // second level friends
        this.userService.createUser(PUBLIC_KEY_USER_4, CONTACTS_2.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_5, CONTACTS_2.get(1));


        final ImportResponse importResponse1 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        final ImportResponse importResponse2 = importService.importContacts(userFriend, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);

        assertThat(contactsCount).isEqualTo(CONTACTS_1.size()); //returns only first level, it is used for count of how many contacts user imported

        final List<String> contacts = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();

        assertThat(contacts).hasSize(CONTACTS_1.size() + CONTACTS_2.size());
        final List<String> contactsSorted = contacts.stream().sorted().toList();
        assertThat(contactsSorted.get(0)).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(contactsSorted.get(1)).isEqualTo(PUBLIC_KEY_USER_3);
        assertThat(contactsSorted.get(2)).isEqualTo(PUBLIC_KEY_USER_4);
        assertThat(contactsSorted.get(3)).isEqualTo(PUBLIC_KEY_USER_5);
    }

    @Test
    void testRetrieveFirstLevelContacts_shouldRetrieveFirstLevelContacts() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        // first level friends
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        final User userFriend = this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));

        // second level friends
        this.userService.createUser(PUBLIC_KEY_USER_4, CONTACTS_2.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_5, CONTACTS_2.get(1));


        final ImportResponse importResponse1 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        final ImportResponse importResponse2 = importService.importContacts(userFriend, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);

        assertThat(contactsCount).isEqualTo(CONTACTS_1.size()); //returns only first level, it is used for count of how many contacts user imported

        final List<String> contacts = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.FIRST).get().toList();

        assertThat(contacts).hasSize(CONTACTS_1.size());
        final List<String> contactsSorted = contacts.stream().sorted().toList();
        assertThat(contactsSorted.get(0)).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(contactsSorted.get(1)).isEqualTo(PUBLIC_KEY_USER_3);
    }

    @Test
    void testRetrieveSecondLevelContacts_shouldRetrieveSecondLevelContacts() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        // first level friends
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        final User userFriend = this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));

        // second level friends
        this.userService.createUser(PUBLIC_KEY_USER_4, CONTACTS_2.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_5, CONTACTS_2.get(1));


        final ImportResponse importResponse1 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        final ImportResponse importResponse2 = importService.importContacts(userFriend, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);

        assertThat(contactsCount).isEqualTo(CONTACTS_1.size()); //returns only first level, it is used for count of how many contacts user imported

        final List<String> contacts = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.SECOND).get().toList();

        assertThat(contacts).hasSize(CONTACTS_2.size());
        final List<String> contactsSorted = contacts.stream().sorted().toList();
        assertThat(contactsSorted.get(0)).isEqualTo(PUBLIC_KEY_USER_4);
        assertThat(contactsSorted.get(1)).isEqualTo(PUBLIC_KEY_USER_5);
    }

    @Test
    void testRemoving_shouldBeDeleted() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0));
        final User userFriend = this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1));
        this.userService.createUser(PUBLIC_KEY_USER_4, CONTACTS_2.get(0));
        this.userService.createUser(PUBLIC_KEY_USER_5, CONTACTS_2.get(1));

        ImportResponse importResponse1 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        ImportResponse importResponse2 = importService.importContacts(user, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();

        final int contactsCount = contactService.getContactsCountByHashFrom(HASH_USER);
        assertThat(contactsCount).isEqualTo(CONTACTS_1.size() + CONTACTS_2.size());

        //deleting contact by hash
        this.contactService.deleteContactByHash(HASH_USER, CONTACTS_1.get(0));

        final int contactsCountAfterOneDelete = contactService.getContactsCountByHashFrom(HASH_USER);
        final List<String> contactsAfterOneDelete = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();
        assertThat(contactsCountAfterOneDelete).isEqualTo(CONTACTS_1.size() + CONTACTS_2.size() - 1);
        assertThat(contactsAfterOneDelete).doesNotContain(CONTACTS_1.get(0));

        //deleting contacts by hashes
        this.contactService.deleteContacts(user, CreateRequestTestUtil.createDeleteContactsRequest(List.of(userFriend.getHash())));
        final int contactsCountAfterTwoDelete = contactService.getContactsCountByHashFrom(HASH_USER);
        final List<String> contactsAfterTwoDelete = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();
        assertThat(contactsCountAfterTwoDelete).isEqualTo(CONTACTS_2.size());
        assertThat(contactsAfterTwoDelete).doesNotContain(CONTACTS_1.get(0), CONTACTS_1.get(0));

        //deleting everything
        this.contactService.deleteAllContacts(PUBLIC_KEY_USER_1);
        final int contactsCountAfterRemovingAll = contactService.getContactsCountByHashFrom(HASH_USER);
        final List<String> contactsAfterRemovingAll = contactService.retrieveContactsByUser(user, 0, 10, ConnectionLevel.ALL).get().toList();
        assertThat(contactsCountAfterRemovingAll).isEqualTo(0);
        assertThat(contactsAfterRemovingAll).isEmpty();
    }

    @Test
    void testRetrieveCommonContacts_shouldRetrieveCommonContacts() {
        final User mainUser = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        ImportResponse importResponse1 = importService.importContacts(mainUser, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        ImportResponse importResponse2 = importService.importContacts(mainUser, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        final User userFriend = this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(1));
        ImportResponse importResponse3 = importService.importContacts(userFriend, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();
        assertThat(importResponse3.imported()).isTrue();

        final var commonContactsResponse = this.contactService.retrieveCommonContacts(
                PUBLIC_KEY_USER_1,
                CreateRequestTestUtil.createCommonContactsRequest(List.of(PUBLIC_KEY_USER_2))
        );
        final var contacts = commonContactsResponse.commonContacts().get(0);
        final var common = contacts.common().hashes().stream().sorted().toList();

        assertThat(contacts.publicKey()).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(common.get(0)).isEqualTo(CONTACTS_2.get(0));
        assertThat(common.get(1)).isEqualTo(CONTACTS_2.get(1));
    }

    @Test
    void testRetrieveCommonContacts_invalidInput_sentOwnPublicKey_shouldRemoveOwnerPublicKeyAndProcessOnlyOthers() {
        final User mainUser = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);
        ImportResponse importResponse1 = importService.importContacts(mainUser, CreateRequestTestUtil.createImportRequest(CONTACTS_1));
        ImportResponse importResponse2 = importService.importContacts(mainUser, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        final User userFriend = this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(1));
        ImportResponse importResponse3 = importService.importContacts(userFriend, CreateRequestTestUtil.createImportRequest(CONTACTS_2));

        assertThat(importResponse1.imported()).isTrue();
        assertThat(importResponse2.imported()).isTrue();
        assertThat(importResponse3.imported()).isTrue();

        final var commonContactsResponse = this.contactService.retrieveCommonContacts(
                PUBLIC_KEY_USER_1,
                CreateRequestTestUtil.createCommonContactsRequest(List.of(PUBLIC_KEY_USER_2, PUBLIC_KEY_USER_1, PUBLIC_KEY_USER_1))
        );

        final var contacts = commonContactsResponse.commonContacts().get(0);
        final var common = contacts.common().hashes().stream().sorted().toList();

        assertThat(commonContactsResponse.commonContacts()).hasSize(1);
        assertThat(contacts.publicKey()).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(common.get(0)).isEqualTo(CONTACTS_2.get(0));
        assertThat(common.get(1)).isEqualTo(CONTACTS_2.get(1));
    }

    @Test
    void testRetrieveFirebaseTokensByGroupUuid_shouldRetrieveFirebaseTokens() {
        final User mainUser = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER, new CreateUserRequest(FIREBASE_TOKEN_4));
        importService.importContacts(mainUser, CreateRequestTestUtil.createImportRequest(List.of(GROUP_UUID)));

        final User groupMember1 = this.userService.createUser(PUBLIC_KEY_USER_2, PHONE_1, new CreateUserRequest(FIREBASE_TOKEN_1));
        importService.importContacts(groupMember1, CreateRequestTestUtil.createImportRequest(List.of(GROUP_UUID)));

        final User groupMember2 = this.userService.createUser(PUBLIC_KEY_USER_3, PHONE_2, new CreateUserRequest(FIREBASE_TOKEN_2));
        importService.importContacts(groupMember2, CreateRequestTestUtil.createImportRequest(List.of(GROUP_UUID)));

        final User notMember = this.userService.createUser(PUBLIC_KEY_USER_4, PHONE_3, new CreateUserRequest(FIREBASE_TOKEN_3));
        importService.importContacts(notMember, CreateRequestTestUtil.createImportRequest(List.of(PUBLIC_KEY_USER_1)));

        final Set<String> membersFirebaseTokens = contactRepository.retrieveGroupMembersFirebaseTokens(GROUP_UUID, PUBLIC_KEY_USER_1);

        assertThat(membersFirebaseTokens).hasSize(2);
        assertThat(membersFirebaseTokens).containsOnly(groupMember1.getFirebaseToken(), groupMember2.getFirebaseToken());
        assertThat(membersFirebaseTokens).doesNotContain(FIREBASE_TOKEN_3, FIREBASE_TOKEN_4);
    }

    @Test
    void testRetrieveFirebaseTokensByHashes_shouldRetrieveFirebaseTokens() {
        final User user1 = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER, new CreateUserRequest(FIREBASE_TOKEN_1));
        final User user2 = this.userService.createUser(PUBLIC_KEY_USER_2, CONTACTS_1.get(0), new CreateUserRequest(FIREBASE_TOKEN_2));
        final User user3 = this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_1.get(1), new CreateUserRequest(FIREBASE_TOKEN_3));
        final User user4 = this.userService.createUser(PUBLIC_KEY_USER_3, CONTACTS_2.get(0), new CreateUserRequest(FIREBASE_TOKEN_4));

        final String user1Hash = user1.getHash();
        final String user2Hash = user2.getHash();
        final String user3Hash = user3.getHash();
        final String user4Hash = user4.getHash();

        final Set<String> result1 = this.contactRepository.retrieveFirebaseTokensByHashes(Set.of(user1Hash));
        assertThat(result1).hasSize(1);
        assertThat(result1).containsOnly(FIREBASE_TOKEN_1);

        final Set<String> result2 = this.contactRepository.retrieveFirebaseTokensByHashes(Set.of(user1Hash, user2Hash, user3Hash));
        assertThat(result2).hasSize(3);
        assertThat(result2).containsOnly(FIREBASE_TOKEN_1, FIREBASE_TOKEN_2, FIREBASE_TOKEN_3);

        final Set<String> result3 = this.contactRepository.retrieveFirebaseTokensByHashes(Set.of(user1Hash, user2Hash, user3Hash, user4Hash));
        assertThat(result3).hasSize(4);
        assertThat(result3).containsOnly(FIREBASE_TOKEN_1, FIREBASE_TOKEN_2, FIREBASE_TOKEN_3, FIREBASE_TOKEN_4);
    }


}
