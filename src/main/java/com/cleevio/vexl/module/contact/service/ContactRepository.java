package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.entity.UserContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

interface ContactRepository extends JpaRepository<UserContact, Long>, JpaSpecificationExecutor<UserContact> {

    boolean existsByHashFromAndHashTo(byte[] hashFrom, byte[] hashTo);

    @Transactional
    @Modifying
    @Query("delete from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :publicKey) ")
    void deleteAllByPublicKey(byte[] publicKey);

    @Transactional
    @Modifying
    @Query("delete from UserContact uc " +
            "where uc.hashTo in ( " +
            "select u.hash from User u " +
            "where u.publicKey in (:publicKeys) ) " +
            "AND uc.hashFrom = :hash ")
    void deleteContacts(byte[] hash, List<byte[]> publicKeys);

    @Query("select count(uc) from UserContact uc where uc.hashFrom = :hash ")
    int countContactsByHash(byte[] hash);
}
