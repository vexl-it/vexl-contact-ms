package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.entity.VContact;
import com.cleevio.vexl.module.contact.enums.ConnectionLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface VContactRepository extends JpaRepository<VContact, Long>, JpaSpecificationExecutor<VContact> {

    @Query("select v.publicKey from VContact v where v.myPublicKey = :myPublicKey AND v.level in (:level) ")
    Page<byte[]> findPublicKeysByMyPublicKeyAndLevel(byte[] myPublicKey, List<ConnectionLevel> level, Pageable pageable);
}