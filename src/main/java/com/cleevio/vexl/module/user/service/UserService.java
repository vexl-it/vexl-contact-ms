package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for creating, searching and deleting of users.
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContactService contactService;

    @Transactional(rollbackFor = Exception.class)
    public void createUser(String publicKeyString, String hashString) {

        byte[] hash = EncryptionUtils.decodeBase64String(hashString);

        Optional<User> userByHash = this.userRepository.findByHash(hash);
        if (userByHash.isPresent()) {
            log.info("FacebookId or phone number is already in use by another user. Hash string: {}. Removing this user and create new one.",
                    hashString);
            this.removeUserAndContacts(userByHash.get());
        }

        byte[] publicKey = EncryptionUtils.decodeBase64String(publicKeyString);

        log.info("Creating an user {} ",
                publicKey);

        User savedUser = this.userRepository.save(User.builder()
                .publicKey(publicKey)
                .hash(hash)
                .build()
        );

        log.info("User id - {} created",
                savedUser.getId());
    }

    @Transactional(readOnly = true)
    public Optional<User> findByPublicKeyAndHash(String publicKey, String hash) {
        byte[] publicKeyByte = EncryptionUtils.decodeBase64String(publicKey);
        byte[] hashByte = EncryptionUtils.decodeBase64String(hash);

        return this.userRepository.findUserByPublicKeyAndHash(publicKeyByte, hashByte);
    }

    @Transactional(readOnly = true)
    public boolean existsByPublicKeyAndHash(String publicKey, String hash) {
        byte[] publicKeyByte = EncryptionUtils.decodeBase64String(publicKey);
        byte[] hashByte = EncryptionUtils.decodeBase64String(hash);

        return this.userRepository.existsByPublicKeyAndHash(publicKeyByte, hashByte);
    }

    public void removeUserAndContacts(User user) {
        log.info("Removing user with id {} and all his contacts",
                user.getId());
        this.contactService.deleteAllContacts(user.getPublicKey());
        this.userRepository.delete(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return this.userRepository.save(user);
    }
}
