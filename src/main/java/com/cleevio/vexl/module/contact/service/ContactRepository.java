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

    @Transactional
    @Modifying
    @Query("delete from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :publicKey) ")
    void deleteAllByPublicKey(String publicKey);

    @Transactional
    @Modifying
    @Query("""
            delete from UserContact uc 
            where uc.hashTo in ( 
            select u.hash from User u 
            where u.publicKey in (:publicKeys)) 
            AND uc.hashFrom = :hash
            """)
    void deleteContacts(String hash, List<String> publicKeys);

    @Query("select count(distinct uc) from UserContact uc where uc.hashFrom = :hash ")
    int countContactsByHash(String hash);

    @Query("""
            select distinct uc.hashTo from UserContact uc where uc.hashTo in 
            (select uc.hashTo from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :ownerPublicKey)) 
            and uc.hashTo in 
            (select uc.hashTo from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :publicKey))
            """)
    List<String> retrieveCommonContacts(String ownerPublicKey, String publicKey);

    @Query("""
            select distinct uc from UserContact uc where uc.hashTo in 
            (select uc.hashTo from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :ownerPublicKey)) 
            and uc.hashTo in 
            (select uc.hashTo from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :publicKeys))
            """)
    List<UserContact> retrieveCommonContacts(String ownerPublicKey, List<String> publicKeys);

    @Query("""
            select distinct uc.hashTo from UserContact uc where uc.hashFrom = :hash 
            and uc.hashTo in (select g.uuid from Group g where g.expirationAt > (extract(epoch from now())) ) 
            """
    )
    List<String> getGroupsUuidsByHash(String hash);

    @Transactional
    @Modifying
    @Query("delete from UserContact uc where uc.hashFrom = :hash and uc.hashTo = :contactHash")
    void deleteContactByHash(String hash, String contactHash);

    @Query("""
            select u.publicKey from User u 
            inner join UserContact uc on u.hash = uc.hashFrom 
            where uc.hashTo = :groupUuidHash and u.publicKey not in (:publicKeys)
            """
    )
    List<String> retrieveNewGroupMembers(String groupUuidHash, List<String> publicKeys);

    @Query("select uc.hashTo from UserContact uc where uc.hashFrom = :hash and uc.hashTo in (:trimContacts) ")
    Set<String> retrieveExistingContacts(String hash, List<String> trimContacts);
}
