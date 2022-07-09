package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.common.enums.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.group.dto.mapper.GroupMapper;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import com.cleevio.vexl.module.group.entity.Group;
import com.cleevio.vexl.module.group.enums.GroupAdvisoryLock;
import com.cleevio.vexl.module.group.event.ImportGroupEvent;
import com.cleevio.vexl.module.group.exception.GroupNotFoundException;
import com.cleevio.vexl.module.group.util.QrCodeUtil;
import com.cleevio.vexl.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final AdvisoryLockService advisoryLockService;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Group createGroup(User user, CreateGroupRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.GROUP,
                GroupAdvisoryLock.CREATE_GROUP.name()
        );

        Group group = groupMapper.mapSingleToGroup(request);
        group.setCode(QrCodeUtil.generateQRCode());
        group.setCreatedBy(user.getPublicKey());

        return this.groupRepository.save(group);
    }

    @Transactional
    public void joinGroup(User user, JoinGroupRequest request) {
        if (!this.groupRepository.existsByUuid(request.groupUuid())) {
            log.warn("Group [{}] was not found.",
                    request.groupUuid());
            throw new GroupNotFoundException();
        }

        applicationEventPublisher.publishEvent(new ImportGroupEvent(request.groupUuid(), user));
    }
}
