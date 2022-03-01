package com.cleevio.vexl.common;

import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.facebook.service.FacebookService;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

public class BaseControllerTest {

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


    @BeforeEach
    @SneakyThrows
    public void setup() {


    }
}
