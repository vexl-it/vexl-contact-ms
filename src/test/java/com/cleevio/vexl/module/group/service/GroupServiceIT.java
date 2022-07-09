package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.entity.Group;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.util.CreateRequestTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupServiceIT {

    private final static String PUBLIC_KEY = "dummy_public_key";
    private final static String HASH = "dummy_hash";
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final UserService userService;

    @Autowired
    public GroupServiceIT(GroupService groupService, GroupRepository groupRepository, UserService userService) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @Test
    void testCreateGroup_shouldBeCreated() {
        final User user = userService.createUser(PUBLIC_KEY, HASH);
        final CreateGroupRequest createGroupRequest = CreateRequestTestUtil.createCreateGroupRequest();
        final Group group = this.groupService.createGroup(user, createGroupRequest);

        assertThat(group.getUuid()).isNotBlank();
        assertThat(group.getName()).isEqualTo(createGroupRequest.name());
        assertThat(group.getLogoUrl()).isEqualTo(createGroupRequest.logo());
        assertThat(group.getExpirationAt()).isEqualTo(createGroupRequest.expiration());
        assertThat(group.getClosureAt()).isEqualTo(createGroupRequest.closureAt());
        assertThat(group.getCreatedAt()).isNotNull();
        assertThat(group.getCreatedBy()).isEqualTo(PUBLIC_KEY);
    }
}
