package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.constant.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.stats.constant.StatsKey;
import com.cleevio.vexl.module.stats.dto.StatsDto;
import com.cleevio.vexl.module.user.constant.UserAdvisoryLock;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for creating, searching and deleting of users.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AdvisoryLockService advisoryLockService;

    @Transactional
    public User createUser(final String publicKey, final String hash) {
        return createUser(publicKey, hash, new CreateUserRequest(null));
    }

    @Transactional
    public User createUser(final String publicKey, final String hash, @Valid CreateUserRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.USER,
                UserAdvisoryLock.CREATE_USER.name(),
                publicKey
        );

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

    @Transactional
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
        advisoryLockService.lock(
                ModuleLockNamespace.USER,
                UserAdvisoryLock.UPDATE_USER.name(),
                publicKey
        );

        final User user = this.userRepository.findUserByPublicKeyAndHash(publicKey, hash)
                .orElseThrow(UserNotFoundException::new);
        user.setFirebaseToken(request.firebaseToken());
    }

    @Transactional
    public void deleteUnregisteredToken(final String firebaseToken) {
        this.userRepository.unregisterFirebaseTokens(firebaseToken);
    }

    @Transactional(readOnly = true)
    public List<StatsDto> retrieveStats(final StatsKey... statsKeys) {
        final List<StatsDto> statsDtos = new ArrayList<>();
        Arrays.stream(statsKeys).forEach(statKey -> {
            switch (statKey) {
                case ALL_TIME_USERS_COUNT -> statsDtos.add(new StatsDto(
                        StatsKey.ALL_TIME_USERS_COUNT,
                        this.userRepository.getAllTimeUsersCount()
                ));
                case ACTIVE_USERS_COUNT -> statsDtos.add(new StatsDto(
                        StatsKey.ACTIVE_USERS_COUNT,
                        this.userRepository.getActiveUsersCount()
                ));
            }
        });
        return statsDtos;
    }
}
