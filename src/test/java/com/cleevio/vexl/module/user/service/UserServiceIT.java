package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.util.CreateRequestTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceIT {

    private final static String PUBLIC_KEY_USER_1 = "dummy_public_key";

    private final static String PUBLIC_KEY_USER_2 = "dummy_public_key_2";
    private final static String HASH_USER = "dummy_hash";
    private final static String FIREBASE_TOKEN = "dummy_firebase_token";
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceIT(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Test
    void testCreateUser_shouldBeCreated() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);

        final User savedUser = this.userRepository.findById(user.getId()).get();
        final int size = this.userRepository.findAll().size();

        assertThat(size).isEqualTo(1);
        assertThat(savedUser.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_1);
        assertThat(savedUser.getHash()).isEqualTo(HASH_USER);
    }

    @Test
    void testCreateUserWithFirebaseToken_shouldBeCreated() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER, CreateRequestTestUtil.createCreateUserRequest(FIREBASE_TOKEN));

        final User savedUser = this.userRepository.findById(user.getId()).get();
        final int size = this.userRepository.findAll().size();

        assertThat(size).isEqualTo(1);
        assertThat(savedUser.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_1);
        assertThat(savedUser.getHash()).isEqualTo(HASH_USER);
        assertThat(savedUser.getFirebaseToken()).isEqualTo(FIREBASE_TOKEN);
    }

    @Test
    void testRecreateUser_shouldBeCreated() {
        final User user = this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);

        final User savedUser = this.userRepository.findById(user.getId()).get();

        assertThat(savedUser.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_1);
        assertThat(savedUser.getHash()).isEqualTo(HASH_USER);

        this.userService.createUser(PUBLIC_KEY_USER_2, HASH_USER);

        final List<User> allUsers = this.userRepository.findAll();
        assertThat(allUsers).hasSize(1);
        assertThat(allUsers.get(0).getPublicKey()).isEqualTo(PUBLIC_KEY_USER_2);
        assertThat(allUsers.get(0).getHash()).isEqualTo(HASH_USER);

    }

    @Test
    void testFindUserByPublicKeyAndHash_shouldBeFound() {
        this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);

        final User savedUser = this.userService.findByPublicKeyAndHash(PUBLIC_KEY_USER_1, HASH_USER).get();

        assertThat(savedUser.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_1);
        assertThat(savedUser.getHash()).isEqualTo(HASH_USER);
    }

    @Test
    void testDeleteUser_shouldBeDeleted() {
        this.userService.createUser(PUBLIC_KEY_USER_1, HASH_USER);

        final User savedUser = this.userService.findByPublicKeyAndHash(PUBLIC_KEY_USER_1, HASH_USER).get();

        assertThat(savedUser.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_1);
        assertThat(savedUser.getHash()).isEqualTo(HASH_USER);

        this.userService.removeUserAndContacts(savedUser);

        final List<User> allUsers = this.userRepository.findAll();
        assertThat(allUsers).isEmpty();
    }
}
