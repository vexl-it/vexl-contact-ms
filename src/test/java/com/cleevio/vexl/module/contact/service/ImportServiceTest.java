package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.entity.UserContact;
import com.cleevio.vexl.module.contact.exception.ContactsMissingException;
import com.cleevio.vexl.module.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@IntegrationTest
public class ImportServiceTest {

    @Mock
    private ContactRepository contactRepository;

    private ImportService importService;

    private final static String PHONE_NUMBER = "+42065489798";

    @Mock
    private User user;

    @Mock
    private UserContact userContact;

    @Mock
    private ContactService contactService;

    @BeforeEach
    public void setup() {
        this.importService = new ImportService(contactRepository, contactService);

    }

    @Test
    void importContacts() throws ContactsMissingException {
        ImportRequest importRequest = new ImportRequest();
        importRequest.setContacts(Collections.singletonList(PHONE_NUMBER));

        Mockito.when(contactRepository.existsByHashFromAndHashTo(any(), any())).thenReturn(false);
        Mockito.when(contactService.calculateHmacSha256(PHONE_NUMBER)).thenReturn(PHONE_NUMBER.getBytes(StandardCharsets.UTF_8));
        Mockito.when(contactRepository.save(any())).thenReturn(userContact);
        importService.importContacts(user, importRequest);
        Mockito.verify(contactRepository).save(any());

    }
}
