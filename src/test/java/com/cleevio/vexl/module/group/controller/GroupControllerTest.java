package com.cleevio.vexl.module.group.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
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


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GroupController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupControllerTest extends BaseControllerTest {

    private static final User USER;
    private static final Group GROUP;

    private static final String DEFAULT_EP = "/api/v1/group";
    private static final String GROUP_NAME = "dummy_name";
    private static final String GROUP_LOGO = "dummy_logo";
    private static final int EXPIRATION = 46546545;
    private static final int CLOSURE_AT = 1616161156;
    private static final String PUBLIC_KEY = "dummy_public_key";
    private static final CreateGroupRequest CREATE_GROUP_REQUEST;
    private static final CreateGroupRequest CREATE_GROUP_REQUEST_INVALID;

    @MockBean
    private GroupService groupService;

    static {
        CREATE_GROUP_REQUEST = new CreateGroupRequest(
                GROUP_NAME,
                GROUP_LOGO,
                EXPIRATION,
                CLOSURE_AT
        );

        CREATE_GROUP_REQUEST_INVALID = new CreateGroupRequest(
                "",
                GROUP_LOGO,
                EXPIRATION,
                CLOSURE_AT
        );

        USER = new User();
        USER.setPublicKey(PUBLIC_KEY);

        GROUP = new Group();
        GROUP.setName(GROUP_NAME);
        GROUP.setLogoUrl(GROUP_LOGO);
        GROUP.setExpirationAt(EXPIRATION);
        GROUP.setClosureAt(CLOSURE_AT);
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
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CREATE_GROUP_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid", notNullValue()))
                .andExpect(jsonPath("$.name", is(GROUP_NAME)))
                .andExpect(jsonPath("$.expiration", is(EXPIRATION)))
                .andExpect(jsonPath("$.closure", is(CLOSURE_AT)));
    }

    @Test
    @SneakyThrows
    void testCreate_invalidInput_shouldReturn400() {
        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CREATE_GROUP_REQUEST_INVALID)))
                .andExpect(status().isBadRequest());
    }
}
