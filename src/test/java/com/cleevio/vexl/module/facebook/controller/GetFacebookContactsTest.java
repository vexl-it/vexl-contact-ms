package com.cleevio.vexl.module.facebook.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.controller.UserController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class GetFacebookContactsTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/facebook/";
    private static final String TOKEN_URL = "/token/";
    private static final String FB_ID = "109753111611432";
    private static final String TOKEN = "EAALQyH4cEwgBANa2kAuKGf50XmV02pKleLUnKdwadHYujRNmN2hr7I11JPR5HZCgxTfzxVVHLjoRZAhDK0cvXYDEkVYZAeGRZBz50A2VKeHgYSYhVIlxTF7tDp0hDeIjToJ5VSZA71VSrtxZBcKnnPVtaT0mofr0j2byi9PEhw7T5rBl4TmDOmyLRCje9PGNTkbrVXELrFBAZDZD";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();

        Mockito.when(facebookService.retrieveContacts(any(String.class), any(String.class))).thenReturn(facebookUser);
        Mockito.when(userService.findByPublicKey(any(String.class))).thenReturn(Optional.of(getUser()));
    }

//    @Test
//    public void registerNewUser() throws Exception {
//        mvc.perform(get(BASE_URL + FB_ID + TOKEN_URL + TOKEN)
//                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
//                        .header(SecurityFilter.HEADER_PHONE_HASH, PHONE_HASH)
//                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.facebookUser", notNullValue()));
//    }

}
