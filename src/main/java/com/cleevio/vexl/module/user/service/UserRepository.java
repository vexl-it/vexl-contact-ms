package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByPublicKeyAndHash(String publicKey, String hash);

    Optional<User> findUserByPublicKeyAndHash(String publicKey, String hash);

    Optional<User> findByHash(String hash);

    @Modifying
    @Query("update User s set s.firebaseToken = null where s.firebaseToken = :firebaseToken")
    void unregisterFirebaseTokens(String firebaseToken);

    @Query(value = "SELECT last_value from users_id_seq", nativeQuery = true)
    int getAllTimeUsersCount();

    @Query("select count(u) from User u")
    int getActiveUsersCount();
}
