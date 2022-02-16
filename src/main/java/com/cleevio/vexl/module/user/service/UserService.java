package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.user.dto.request.CreateUserRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContactService contactService;

    @Transactional(rollbackFor = Exception.class)
    public User createUser(CreateUserRequest request)
            throws UserAlreadyExistsException {

        log.info("Creating user {} ",
                request.getPublicKey());

        byte[] publicKeyByte = EncryptionUtils.decodeBase64String(request.getPublicKey());
        byte[] hashByte = EncryptionUtils.decodeBase64String(request.getHash());

        if (this.userRepository.existsByPublicKeyAndHash(publicKeyByte, hashByte)) {
            throw new UserAlreadyExistsException();
        }

        return this.userRepository.save(User.builder()
                .publicKey(publicKeyByte)
                .hash(hashByte)
                .build()
        );
    }

    public Optional<User> findByPublicKey(String publicKey) {
        byte[] publicKeyByte = EncryptionUtils.decodeBase64String(publicKey);
        return this.userRepository.findUserByPublicKey(publicKeyByte);
    }

    public void removeUserAndContacts(User user) {
        this.contactService.deleteAllContacts(user.getPublicKey());
        this.userRepository.delete(user);
    }
}
