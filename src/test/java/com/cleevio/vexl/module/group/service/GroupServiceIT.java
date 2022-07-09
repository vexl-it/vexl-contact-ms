package com.cleevio.vexl.module.group.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.entity.Group;
import com.cleevio.vexl.module.group.exception.GroupNotFoundException;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.util.CreateRequestTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupServiceIT {

    private final static String PUBLIC_KEY_USER_1 = "dummy_public_key";
    private final static String HASH_USER_1 = "dummy_hash";
    private final static String PUBLIC_KEY_USER_2 = "dummy_public_key_2";
    private final static String HASH_USER_2 = "dummy_hash_2";
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public GroupServiceIT(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @Test
    void testCreateGroup_shouldBeCreated() {
        final User user = userService.createUser(PUBLIC_KEY_USER_1, HASH_USER_1);
        final CreateGroupRequest createGroupRequest = CreateRequestTestUtil.createCreateGroupRequest();
        final Group group = this.groupService.createGroup(user, createGroupRequest);

        assertThat(group.getUuid()).isNotBlank();
        assertThat(group.getName()).isEqualTo(createGroupRequest.name());
        assertThat(group.getLogoUrl()).isEqualTo(createGroupRequest.logo());
        assertThat(group.getExpirationAt()).isEqualTo(createGroupRequest.expiration());
        assertThat(group.getClosureAt()).isEqualTo(createGroupRequest.closureAt());
        assertThat(group.getCreatedAt()).isNotNull();
        assertThat(group.getCreatedBy()).isEqualTo(PUBLIC_KEY_USER_1);
    }

    @Test
    void testJoinGroupAndRetrieveMyGroups_shouldBeJoinedAndGroupShouldBeFound() {
        final User user = userService.createUser(PUBLIC_KEY_USER_1, HASH_USER_1);
        final User user2 = userService.createUser(PUBLIC_KEY_USER_2, HASH_USER_2);

        final List<Group> groupsBefore = this.groupService.retrieveMyGroups(user);
        assertThat(groupsBefore).hasSize(0);

        final Group group = this.groupService.createGroup(user2, CreateRequestTestUtil.createCreateGroupRequest());
        this.groupService.joinGroup(user, CreateRequestTestUtil.createJoinGroupRequest(group.getUuid()));

        final List<Group> groups = this.groupService.retrieveMyGroups(user);

        assertThat(groups).hasSize(1);

        final Group joinedGroup = groups.get(0);
        assertThat(joinedGroup.getName()).isEqualTo(group.getName());
        assertThat(joinedGroup.getUuid()).isEqualTo(group.getUuid());
        assertThat(joinedGroup.getCode()).isEqualTo(group.getCode());
        assertThat(joinedGroup.getLogoUrl()).isEqualTo(group.getLogoUrl());
        assertThat(joinedGroup.getCreatedAt()).isEqualTo(group.getCreatedAt());
        assertThat(joinedGroup.getExpirationAt()).isEqualTo(group.getExpirationAt());
        assertThat(joinedGroup.getClosureAt()).isEqualTo(group.getClosureAt());
        assertThat(joinedGroup.getCreatedBy()).isEqualTo(group.getCreatedBy());
    }

    @Test
    void testJoinGroup_shouldReturnGroupNotFoundException() {
        final User user = userService.createUser(PUBLIC_KEY_USER_1, HASH_USER_1);
        final var groupUuid = "dummy_uuid";

        assertThrows(
                GroupNotFoundException.class,
                () -> this.groupService.joinGroup(user, CreateRequestTestUtil.createJoinGroupRequest(groupUuid))
        );
    }

    @Test
    void testRetrieveMyGroups_shouldFindAllMyGroups() {
        final User user = userService.createUser(PUBLIC_KEY_USER_1, HASH_USER_1);
        final User user2 = userService.createUser(PUBLIC_KEY_USER_2, HASH_USER_2);
        final Group group = this.groupService.createGroup(user, CreateRequestTestUtil.createCreateGroupRequest());
        final Group group2 = this.groupService.createGroup(user2, CreateRequestTestUtil.createCreateGroupRequest());
        final Group group3 = this.groupService.createGroup(user, CreateRequestTestUtil.createCreateGroupRequest());
        final Group group4 = this.groupService.createGroup(user, CreateRequestTestUtil.createCreateGroupRequest());

        this.groupService.joinGroup(user2, CreateRequestTestUtil.createJoinGroupRequest(group.getUuid()));
        this.groupService.joinGroup(user, CreateRequestTestUtil.createJoinGroupRequest(group2.getUuid()));
        this.groupService.joinGroup(user, CreateRequestTestUtil.createJoinGroupRequest(group3.getUuid()));
        this.groupService.joinGroup(user2, CreateRequestTestUtil.createJoinGroupRequest(group4.getUuid()));

        /**
         * If user creates a group, he is automatically connected to it.
         */

         final List<Group> user1Groups = this.groupService.retrieveMyGroups(user);
         final List<Group> user2Groups = this.groupService.retrieveMyGroups(user2);

         assertThat(user1Groups).hasSize(4);
         assertThat(user2Groups).hasSize(3);
    }
}
