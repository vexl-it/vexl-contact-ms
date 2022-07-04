package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.module.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
}
