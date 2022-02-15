package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByPublicKeyAndHash(byte[] publicKey, byte[] hash);
}
