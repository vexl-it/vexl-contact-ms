package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.common.enums.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.group.dto.mapper.GroupMapper;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import com.cleevio.vexl.module.group.dto.request.LeaveGroupRequest;
import com.cleevio.vexl.module.group.entity.Group;
import com.cleevio.vexl.module.group.enums.GroupAdvisoryLock;
import com.cleevio.vexl.module.group.event.ImportGroupEvent;
import com.cleevio.vexl.module.group.event.LeaveGroupEvent;
import com.cleevio.vexl.module.group.exception.GroupNotFoundException;
import com.cleevio.vexl.module.group.util.QrCodeUtil;
import com.cleevio.vexl.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final AdvisoryLockService advisoryLockService;
    private final ContactService contactService;
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

        Group savedGroup = this.groupRepository.save(group);
        applicationEventPublisher.publishEvent(new ImportGroupEvent(savedGroup.getUuid(), user));
        return savedGroup;
    }

    @Transactional
    public void joinGroup(User user, JoinGroupRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.GROUP,
                GroupAdvisoryLock.JOIN_GROUP.name()
        );

        final String groupUuid = this.groupRepository.findGroupUuidByCode(request.code())
                .orElseThrow(GroupNotFoundException::new);

        applicationEventPublisher.publishEvent(new ImportGroupEvent(groupUuid, user));
    }

    @Transactional(readOnly = true)
    public List<Group> retrieveMyGroups(final User user) {
        final List<String> uuids = this.groupRepository.findAllUuids();
        Map<String, String> uuidAndHashedUuids = new HashMap<>();

        uuids.forEach(uuid -> uuidAndHashedUuids.put(CLibrary.CRYPTO_LIB.sha256_hash(uuid, uuid.length()), uuid));

        final List<String> userGroupUuidHashes = this.contactService.getGroups(user.getHash(), uuidAndHashedUuids.keySet());

        List<String> userGroupUuid = new ArrayList<>();
        userGroupUuidHashes.forEach(uuidHash -> {
            userGroupUuid.add(uuidAndHashedUuids.get(uuidHash));
        });

        return this.groupRepository.findGroupsByUuids(userGroupUuid);
    }

    public void leaveGroup(User user, LeaveGroupRequest request) {
        applicationEventPublisher.publishEvent(new LeaveGroupEvent(user.getHash(), request.groupUuid()));
    }

    @Transactional(readOnly = true)
    public List<Group> retrieveGroupsByUuid(final List<String> groupUuid) {
        return this.groupRepository.findGroupsByUuids(groupUuid);
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> retrieveNewMembers(final List<String> groupUuids, final List<String> publicKeys) {
        Map<String, List<String>> newMembers = new HashMap<>();

        groupUuids.forEach(uuid -> {
            final String uuidHash = CLibrary.CRYPTO_LIB.sha256_hash(uuid, uuid.length());
            newMembers.put(uuid, this.contactService.retrieveNewGroupMembers(uuidHash, publicKeys));
        });

        return newMembers;
    }
}
