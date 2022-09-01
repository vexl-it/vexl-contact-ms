package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.constant.ConnectionLevel;
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
            where uc.hashFrom = :hash and 
            uc.hashTo in (:hashes)
            """)
    void deleteContactsByHashes(String hash, List<String> hashes);

    @Query("select count(distinct uc) from UserContact uc where uc.hashFrom = :hash ")
    int countContactsByHashFrom(String hash);

    @Query("select count(distinct uc) from UserContact uc where uc.hashTo = :hash")
    int countContactsByHashTo(String hash);

    @Query("""
            select distinct uc.hashTo from UserContact uc where uc.hashTo in 
            (select uc.hashTo from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :ownerPublicKey)) 
            and uc.hashTo in 
            (select uc.hashTo from UserContact uc where uc.hashFrom in (select u.hash from User u where u.publicKey = :publicKey))
            """)
    List<String> retrieveCommonContacts(String ownerPublicKey, String publicKey);

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
            select distinct u.publicKey from User u 
            inner join UserContact uc on u.hash = uc.hashFrom 
            where uc.hashTo = :groupUuidHash and u.publicKey not in (:publicKeys)
            """
    )
    List<String> retrieveNewGroupMembers(String groupUuidHash, List<String> publicKeys);

    @Query("""
            select distinct u.publicKey from User u 
            inner join UserContact uc on u.hash = uc.hashFrom 
            where uc.hashTo = :groupUuidHash
            """
    )
    List<String> retrieveAllGroupMembers(String groupUuidHash);

    @Query("select uc.hashTo from UserContact uc where uc.hashFrom = :hash and uc.hashTo in (:trimContacts) ")
    Set<String> retrieveExistingContacts(String hash, List<String> trimContacts);

    @Query("select case when (count(uc) > 0) then true else false end from UserContact uc where uc.hashFrom = :hash and uc.hashTo = :trimContact ")
    boolean existsContact(String hash, String trimContact);

    @Query("select u.firebaseToken from User u where u.hash in (select uc.hashFrom from UserContact uc where uc.hashTo = :hash) and u.publicKey <> :publicKey and u.firebaseToken is not null")
    Set<String> retrieveGroupMembersFirebaseTokens(String hash, String publicKey);

    @Query("""
            select distinct u.firebaseToken from User u 
            JOIN UserContact uc on u.hash = uc.hashFrom 
            where u.hash in (:existingContactHashes) and u.firebaseToken is not null 
            and uc.hashTo = :newUserHash
            """)
    Set<String> retrieveFirebaseTokensByHashes(Set<String> existingContactHashes, String newUserHash);

    @Query("""
            select distinct u.firebaseToken from User u 
            INNER JOIN VContact v on u.publicKey = v.publicKey 
            INNER JOIN User u2 on v.myPublicKey = u2.publicKey 
            INNER JOIN UserContact uc on u2.hash = uc.hashFrom 
            where u.hash <> :newUserHash and u.firebaseToken not in (:firstDegreeFirebaseTokens) 
            and u2.hash in (:existingContactHashes) and u2.firebaseToken is not null 
            and uc.hashTo = :newUserHash and v.level = :level
            """)
    Set<String> retrieveSecondDegreeFirebaseTokensByHashes(Set<String> existingContactHashes, String newUserHash, Set<String> firstDegreeFirebaseTokens, ConnectionLevel level);
}