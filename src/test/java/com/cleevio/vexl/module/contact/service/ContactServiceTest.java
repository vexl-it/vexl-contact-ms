package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@IntegrationTest
public class ContactServiceTest {

    @Value("${hmac.secret.key}")
    private String secretKey;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private VContactRepository vContactRepository;

    private ContactService contactService;

    @Mock
    private User user;

    private final static String PHONE_NUMBER = "+42065489798";

    @BeforeEach
    public void setup() {
        this.contactService = new ContactService(secretKey, contactRepository, vContactRepository);

    }

    @Test
    void deleteAllContacts_test() {
        contactService.deleteAllContacts(PHONE_NUMBER.getBytes(StandardCharsets.UTF_8));
        Mockito.verify(contactRepository).deleteAllByPublicKey(any());

    }

    @Test
    void deleteContacts_test() {
        DeleteContactsRequest deleteContactsRequest = new DeleteContactsRequest();
        deleteContactsRequest.setContactsToDelete(Collections.singletonList(PHONE_NUMBER));
        contactService.deleteContacts(user, deleteContactsRequest);
        Mockito.verify(contactRepository).deleteContacts(any(), any());

    }
}
