package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.dto.request.CreateUserRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserErrorType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class CreatePostTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/user";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();

        Mockito.when(userService.createUser(any(CreateUserRequest.class))).thenReturn(getUser());
    }

    @Test
    public void createNewUser() throws Exception {
        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_PHONE_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new CreateUserRequest(PUBLIC_KEY, PHONE_HASH))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.publicKey", notNullValue()))
                .andExpect(jsonPath("$.hash", notNullValue()));
    }

    @Test
    public void registerUserWithExistingUsername() throws Exception {
        Mockito.when(userService.createUser(any(CreateUserRequest.class))).thenThrow(UserAlreadyExistsException.class);

        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_PHONE_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new CreateUserRequest(PUBLIC_KEY, PHONE_HASH))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is(ApiException.Module.USER.getErrorCode() + UserErrorType.USER_DUPLICATE.getCode())))
                .andExpect(jsonPath("$.message[0]", is(UserErrorType.USER_DUPLICATE.getMessage())));
    }

}
