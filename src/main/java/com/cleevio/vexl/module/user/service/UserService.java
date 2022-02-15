package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.CreateUserRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public User createUser(CreateUserRequest request)
            throws UserAlreadyExistsException {

        log.info("Creating user {} ",
                request.getPublicKey());

        if (this.userRepository.existsByPublicKeyAndHash(request.getPublicKey(), request.getHash())) {
            throw new UserAlreadyExistsException();
        }

        return this.userRepository.save(User.builder()
                .publicKey(request.getPublicKey())
                .hash(request.getHash())
                .build()
        );
    }
}
