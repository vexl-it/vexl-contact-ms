package com.cleevio.vexl.module.contact.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
import com.cleevio.vexl.module.contact.dto.response.ImportResponse;
import com.cleevio.vexl.module.user.entity.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
public class ImportPostTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/contacts";

    @BeforeEach
    @SneakyThrows
    public void setup() {
        super.setup();


        Mockito.when(importService.importContacts(any(User.class), any(ImportRequest.class))).thenReturn(new ImportResponse(true, "Success"));
        Mockito.when(contactService.retrieveContactsByUser(any(User.class), anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(Collections.singletonList("+42045464465".getBytes())));
        Mockito.when(contactService.retrieveNewContacts(any(User.class), any(NewContactsRequest.class))).thenReturn(Collections.singletonList("+42045464465"));

    }


    @Test
    public void importContacts() throws Exception {
        ImportRequest importRequest = new ImportRequest();
        importRequest.setContacts(Arrays.asList("+42085285282", "+42058965236"));

        mvc.perform(post(BASE_URL + "/import")
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(importRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", notNullValue()))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    public void getMyContacts() throws Exception {

        mvc.perform(get(BASE_URL + "/me")
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].publickey", notNullValue()));
    }

    @Test
    public void getNotImportedContacts() throws Exception {
        NewContactsRequest contactsRequest = new NewContactsRequest();
        contactsRequest.setContacts(List.of("+11231321231", "+1113241231"));

        mvc.perform(post(BASE_URL + "/not-imported/")
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(contactsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newContacts", notNullValue()));
    }

    @Test
    public void getNotImportedContactsWrongRequest_emptyContacts() throws Exception {
        mvc.perform(post(BASE_URL + "/not-imported/")
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new NewContactsRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void importContactsWrongRequest_emptyContacts() throws Exception {
        mvc.perform(post(BASE_URL + "/import")
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY)
                        .header(SecurityFilter.HEADER_HASH, PHONE_HASH)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new ImportRequest())))
                .andExpect(status().isBadRequest());
    }


}
