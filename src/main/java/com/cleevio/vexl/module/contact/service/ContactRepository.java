package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

interface ContactRepository extends JpaRepository<UserContact, Long>, JpaSpecificationExecutor<UserContact> {

    boolean existsByHashFromAndHashTo(byte[] hashFrom, byte[] hashTo);

    @Query(value = "select x.hash_to from ( " +
            "select distinct uc.hash_to from user_contact uc " +
            "join users u on u.hash = uc.hash_from " +
            "where u.public_key = :publicKey " +
            "union " +
            "select distinct uc2.hash_to from user_contact uc " +
            "left join user_contact uc2 on uc.hash_to = uc2.hash_from " +
            "join users u on u.hash = uc.hash_from " +
            "where u.public_key = :publicKey) x " +
            "where x.hash_to is not null ",
            nativeQuery = true)
    Set<byte[]> findAllContactsByPublicKey(byte[] publicKey);

    @Transactional
    @Modifying
    @Query("delete from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :publicKey) ")
    void deleteAllByPublicKey(byte[] publicKey);

    @Transactional
    @Modifying
    @Query("delete from UserContact uc where uc.hashTo in (:contacts) AND uc.hashFrom = :hash ")
    void deleteContacts(byte[] hash, List<byte[]> contacts);
}
