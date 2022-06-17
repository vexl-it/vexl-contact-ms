package com.cleevio.vexl.common;

import com.cleevio.vexl.common.service.SignatureService;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.facebook.dto.FacebookUser;
import com.cleevio.vexl.module.facebook.service.FacebookService;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class BaseControllerTest {

    protected static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==";
    protected static final String PHONE_HASH = "GCzF7P15aLtu+LG6itgRfRKpOO+KKrdKZAnPzmTl1Fs=";
    protected static final String SIGNATURE = "/ty+wIsnpJu5XAcqTYs9FspaJct6YipVpIMqZTrMOglkisoU5E9jy5OiTVG/Gg5jVy+zEyc9KTHwJmIBcwlvDQ==";

    @Autowired
    protected MockMvc mvc;

    @MockBean
    protected UserService userService;

    @MockBean
    protected ContactService contactService;

    @MockBean
    protected FacebookService facebookService;

    @MockBean
    protected ImportService importService;

    @Mock
    protected User user;

    @Mock
    protected FacebookUser facebookUser;

    @MockBean
    protected SignatureService signatureService;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    @SneakyThrows
    public void setup() {

        Mockito.when(signatureService.isSignatureValid(any(String.class), any(String.class), any(String.class))).thenReturn(true);
        Mockito.when(userService.findByPublicKeyAndHash(any(String.class), any(String.class))).thenReturn(Optional.of(getUser()));
        Mockito.when(userService.existsByPublicKeyAndHash(any(String.class), any(String.class))).thenReturn(true);
    }

    public User getUser() {
        return User.builder()
                .id(1L)
                .publicKey(PUBLIC_KEY)
                .hash(PHONE_HASH)
                .build();
    }

    public FacebookUser getFacebookUser() {
        FacebookUser facebookUser = new FacebookUser();
        facebookUser.setId("1");
        facebookUser.setName("Marting");
        return facebookUser;
    }

    /**
     * Entity to json string body helper
     *
     * @param obj Entity
     * @return JSON string
     */
    protected String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
