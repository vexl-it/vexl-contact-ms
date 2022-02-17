package com.cleevio.vexl.module.contact.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class NewContactsResponse {

    private final List<String> newContacts;
}
