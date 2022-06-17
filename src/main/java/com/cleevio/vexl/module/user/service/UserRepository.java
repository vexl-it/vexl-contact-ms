package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByPublicKeyAndHash(String publicKey, String hash);

    Optional<User> findUserByPublicKeyAndHash(String publicKey, String hash);

    Optional<User> findByHash(String hash);
}
