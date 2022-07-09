package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.module.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    boolean existsByUuid(String groupUuid);

    @Query("select g from Group g where g.uuid in (:uuids)")
    List<Group> findGroupsByUuids(List<String> uuids);

    @Query("select distinct g.uuid from Group g")
    List<String> findAllUuids();
}
