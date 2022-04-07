package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class CreatePostTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/users";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();

        Mockito.when(userService.existsByPublicKeyAndHash(any(String.class), any(String.class))).thenReturn(false);
    }

    @Test
    public void createNewUser() throws Exception {
        mvc.perform(post(BASE_URL)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
