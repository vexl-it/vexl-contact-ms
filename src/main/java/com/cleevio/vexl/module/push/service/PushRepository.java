package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.module.push.entity.Push;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface PushRepository extends JpaRepository<Push, Long>, JpaSpecificationExecutor<Push> {

    @Query("select p from Push p where p.groupUuid in (select g.uuid from Group g) ")
    List<Push> findAllPushNotificationsWithExistingGroup();

    @Query("delete from Push p where p.groupUuid not in (select g.uuid from Group g) ")
    @Modifying
    void deleteOrphans();
}
