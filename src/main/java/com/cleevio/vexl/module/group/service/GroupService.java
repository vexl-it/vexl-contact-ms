package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.common.constant.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.group.dto.mapper.GroupMapper;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.ExpiredGroupsRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import com.cleevio.vexl.module.group.dto.request.LeaveGroupRequest;
import com.cleevio.vexl.module.group.dto.request.NewMemberRequest;
import com.cleevio.vexl.module.group.entity.Group;
import com.cleevio.vexl.module.group.constant.GroupAdvisoryLock;
import com.cleevio.vexl.module.group.event.GroupImportedEvent;
import com.cleevio.vexl.module.group.event.GroupLeftEvent;
import com.cleevio.vexl.module.group.exception.GroupNotFoundException;
import com.cleevio.vexl.module.group.util.CodeUtil;
import com.cleevio.vexl.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GroupService {

//todo ještě domyslet jak dělat ty groups

    private final AdvisoryLockService advisoryLockService;
    private final ContactService contactService;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Group createGroup(final User user, @Valid final CreateGroupRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.GROUP,
                GroupAdvisoryLock.CREATE_GROUP.name()
        );

        Group group = groupMapper.mapSingleToGroup(request);
        group.setCode(CodeUtil.generateQRCode());
        group.setCreatedBy(user.getPublicKey());

        final Group savedGroup = this.groupRepository.save(group);
        applicationEventPublisher.publishEvent(new GroupImportedEvent(savedGroup.getUuid(), user));
        return savedGroup;
    }

    @Transactional
    public void joinGroup(final User user, @Valid final JoinGroupRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.GROUP,
                GroupAdvisoryLock.JOIN_GROUP.name()
        );

        final String groupUuid = this.groupRepository.findGroupUuidByCode(request.code())
                .orElseThrow(GroupNotFoundException::new);

        applicationEventPublisher.publishEvent(new GroupImportedEvent(groupUuid, user));
    }

    @Transactional(readOnly = true)
    public List<Group> retrieveMyGroups(final User user) {
        final List<String> userGroupUuid = this.contactService.getGroupsUuidsByHash(user.getHash());

        return this.groupRepository.findGroupsByUuids(userGroupUuid);
    }

    public void leaveGroup(final User user, final LeaveGroupRequest request) {
        applicationEventPublisher.publishEvent(new GroupLeftEvent(user.getHash(), request.groupUuid()));
    }

    @Transactional(readOnly = true)
    public Group retrieveGroupByCode(final int code) {
        return this.groupRepository.findGroupsByCode(code);
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> retrieveNewMembers(@Valid final List<NewMemberRequest.GroupRequest> groups, final User user) {
        final Map<String, List<String>> newMembers = new HashMap<>();

        groups.forEach(group -> {
            final String groupUuid = group.groupUuid();
            final List<String> publicKeys = new ArrayList<>(List.of(user.getPublicKey()));
            publicKeys.addAll(group.publicKeys());
            newMembers.put(groupUuid, this.contactService.retrieveNewGroupMembers(groupUuid, publicKeys));
        });

        return newMembers;
    }

    @Transactional(readOnly = true)
    public List<Group> retrieveExpiredGroups(@Valid final ExpiredGroupsRequest request) {
        return this.groupRepository.retrieveExpiredGroups(request.uuids());
    }
}
