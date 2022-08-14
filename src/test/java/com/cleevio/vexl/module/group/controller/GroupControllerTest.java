package com.cleevio.vexl.module.group.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.group.dto.GroupModel;
import com.cleevio.vexl.module.group.dto.mapper.GroupMapper;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import com.cleevio.vexl.module.group.dto.request.LeaveGroupRequest;
import com.cleevio.vexl.module.group.dto.request.NewMemberRequest;
import com.cleevio.vexl.module.group.dto.response.GroupsResponse;
import com.cleevio.vexl.module.group.entity.Group;
import com.cleevio.vexl.module.group.service.GroupService;
import com.cleevio.vexl.module.user.entity.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;


import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GroupController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupControllerTest extends BaseControllerTest {

    private static final User USER;
    private static final Group GROUP;
    private static final GroupModel GROUP_MODEL;

    private static final String DEFAULT_EP = "/api/v1/groups";
    private static final String JOIN_EP = DEFAULT_EP + "/join";
    private static final String ME_EP = DEFAULT_EP + "/me";
    private static final String LEAVE_EP = DEFAULT_EP + "/leave";
    private static final String NEW_MEMBERS_EP = DEFAULT_EP + "/members/new";
    private static final String EXPIRED_EP = DEFAULT_EP + "/expired";
    private static final String GROUP_NAME = "dummy_name";
    private static final String GROUP_LOGO = "dummy_logo";
    private static final String GROUP_UUID = "dummy_group_uuid";
    private static final int EXPIRATION = 46546545;
    private static final int CLOSURE_AT = 1616161156;
    private static final int CODE = 456654;
    private static final int MEMBERS_COUNT = 10;
    private static final String PUBLIC_KEY = "dummy_public_key";
    private static final CreateGroupRequest CREATE_GROUP_REQUEST;
    private static final JoinGroupRequest JOIN_GROUP_REQUEST;
    private static final LeaveGroupRequest LEAVE_GROUP_REQUEST;
    private static final NewMemberRequest NEW_MEMBER_REQUEST;
    private static final NewMemberRequest.GroupRequest GROUP_REQUEST;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupMapper groupMapper;

    static {
        CREATE_GROUP_REQUEST = new CreateGroupRequest(
                GROUP_NAME,
                null,
                EXPIRATION,
                CLOSURE_AT
        );

        JOIN_GROUP_REQUEST = new JoinGroupRequest(
                CODE
        );

        LEAVE_GROUP_REQUEST = new LeaveGroupRequest(
                GROUP_UUID
        );

        GROUP_REQUEST = new NewMemberRequest.GroupRequest(
                GROUP_UUID,
                List.of(PUBLIC_KEY)
        );

        NEW_MEMBER_REQUEST = new NewMemberRequest(
                List.of(GROUP_REQUEST)
        );

        USER = new User();
        USER.setPublicKey(PUBLIC_KEY);

        GROUP = new Group();
        GROUP.setName(GROUP_NAME);
        GROUP.setLogoUrl(GROUP_LOGO);
        GROUP.setExpirationAt(EXPIRATION);
        GROUP.setClosureAt(CLOSURE_AT);
        GROUP.setCode(CODE);

        GROUP_MODEL = new GroupModel(GROUP, MEMBERS_COUNT);
    }

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    @SneakyThrows
    void testCreate_validInput_shouldReturn200() {
        when(groupService.createGroup(any(), any())).thenReturn(GROUP);

        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CREATE_GROUP_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid", notNullValue()))
                .andExpect(jsonPath("$.name", is(GROUP_NAME)))
                .andExpect(jsonPath("$.logoUrl", is(GROUP_LOGO)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.expirationAt", is(EXPIRATION)))
                .andExpect(jsonPath("$.closureAt", is(CLOSURE_AT)))
                .andExpect(jsonPath("$.code", is(CODE)));
    }

    @Test
    @SneakyThrows
    void testJoin_validInput_shouldReturn204() {
        mvc.perform(post(JOIN_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(JOIN_GROUP_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testNewMembers_validInput_shouldReturn200() {
        when(this.groupService.retrieveNewMembers(any(), any())).thenReturn(Map.of(GROUP_UUID, List.of(PUBLIC_KEY)));

        mvc.perform(post(NEW_MEMBERS_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(NEW_MEMBER_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMembers[0].groupUuid", is(GROUP_UUID)))
                .andExpect(jsonPath("$.newMembers[0].publicKeys", is(List.of(PUBLIC_KEY))));
    }

    @Test
    @SneakyThrows
    void testGetMe_shouldReturn200() {
        when(groupService.retrieveMyGroups(any())).thenReturn(List.of(GROUP_MODEL));
        when(groupMapper.mapGroupModelToGroupResponse(List.of(GROUP_MODEL))).thenReturn(List.of(new GroupsResponse.GroupResponse(GROUP, MEMBERS_COUNT)));

        mvc.perform(get(ME_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupResponse[0].uuid", notNullValue()))
                .andExpect(jsonPath("$.groupResponse[0].name", is(GROUP_NAME)))
                .andExpect(jsonPath("$.groupResponse[0].createdAt", notNullValue()))
                .andExpect(jsonPath("$.groupResponse[0].expirationAt", is(EXPIRATION)))
                .andExpect(jsonPath("$.groupResponse[0].closureAt", is(CLOSURE_AT)))
                .andExpect(jsonPath("$.groupResponse[0].code", is(CODE)))
                .andExpect(jsonPath("$.groupResponse[0].memberCount", is(MEMBERS_COUNT)));
    }

    @Test
    @SneakyThrows
    void testGetGroup_shouldReturn200() {
        when(groupService.retrieveGroupByCode(anyInt())).thenReturn(GROUP_MODEL);
        when(groupMapper.mapGroupModelToGroupResponse(List.of(GROUP_MODEL))).thenReturn(List.of(new GroupsResponse.GroupResponse(GROUP, MEMBERS_COUNT)));

        mvc.perform(get(DEFAULT_EP + "?code=783456")
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", notNullValue()))
                .andExpect(jsonPath("$.name", is(GROUP_NAME)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.expirationAt", is(EXPIRATION)))
                .andExpect(jsonPath("$.closureAt", is(CLOSURE_AT)))
                .andExpect(jsonPath("$.code", is(CODE)))
                .andExpect(jsonPath("$.memberCount", is(MEMBERS_COUNT)));
    }

    @Test
    @SneakyThrows
    void testGetExpiredGroups_shouldReturn200() {
        when(groupService.retrieveExpiredGroups(any())).thenReturn(List.of(GROUP_MODEL));
        when(groupMapper.mapGroupModelToGroupResponse(List.of(GROUP_MODEL))).thenReturn(List.of(new GroupsResponse.GroupResponse(GROUP, MEMBERS_COUNT)));

        mvc.perform(post(EXPIRED_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(NEW_MEMBER_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupResponse[0].uuid", notNullValue()))
                .andExpect(jsonPath("$.groupResponse[0].name", is(GROUP_NAME)))
                .andExpect(jsonPath("$.groupResponse[0].createdAt", notNullValue()))
                .andExpect(jsonPath("$.groupResponse[0].expirationAt", is(EXPIRATION)))
                .andExpect(jsonPath("$.groupResponse[0].closureAt", is(CLOSURE_AT)))
                .andExpect(jsonPath("$.groupResponse[0].code", is(CODE)))
                .andExpect(jsonPath("$.groupResponse[0].memberCount", is(MEMBERS_COUNT)));
    }

    @Test
    @SneakyThrows
    void testLeaveGroup_shouldReturn204() {
        mvc.perform(put(LEAVE_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(LEAVE_GROUP_REQUEST)))
                .andExpect(status().isNoContent());
    }
}
