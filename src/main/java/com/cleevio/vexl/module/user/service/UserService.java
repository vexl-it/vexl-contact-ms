package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.CreateUserRequest;
import com.cleevio.vexl.module.user.dto.request.FirebaseTokenUpdateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.event.UserRemovedEvent;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Service for creating, searching and deleting of users.
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public User createUser(final String publicKey, final String hash) {
        return createUser(publicKey, hash, new CreateUserRequest(null));
    }

    @Transactional
    public User createUser(final String publicKey, final String hash, @Valid CreateUserRequest request) {

        final Optional<User> userByHash = this.userRepository.findByHash(hash);
        if (userByHash.isPresent()) {
            log.info("FacebookId or phone number is already in use by another user. Hash string: [{}]. Removing this user and create new one.",
                    hash);
            this.removeUserAndContacts(userByHash.get());
        }

        log.info("Creating an user [{}] ",
                publicKey);

        final User savedUser = this.userRepository.save(
                User.builder()
                        .publicKey(publicKey)
                        .hash(hash)
                        .firebaseToken(request.firebaseToken())
                        .build()
        );

        log.info("User id - [{}] created",
                savedUser.getId());

        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByPublicKeyAndHash(String publicKey, String hash) {
        return this.userRepository.findUserByPublicKeyAndHash(publicKey, hash);
    }

    @Transactional(readOnly = true)
    public boolean existsByPublicKeyAndHash(String publicKey, String hash) {
        return this.userRepository.existsByPublicKeyAndHash(publicKey, hash);
    }

    public void removeUserAndContacts(User user) {
        log.info("Removing user with id {} and all his contacts",
                user.getId());
        this.applicationEventPublisher.publishEvent(new UserRemovedEvent(user.getPublicKey()));
        this.userRepository.delete(user);
    }

    @Transactional
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Transactional
    public void updateFirebaseToken(final String publicKey, final String hash, @Valid final FirebaseTokenUpdateRequest request) {
        final User user = this.userRepository.findUserByPublicKeyAndHash(publicKey, hash)
                .orElseThrow(UserNotFoundException::new);
        user.setFirebaseToken(request.firebaseToken());
    }
}
