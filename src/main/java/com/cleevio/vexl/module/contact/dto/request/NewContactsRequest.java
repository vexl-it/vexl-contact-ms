package com.cleevio.vexl.module.contact.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class NewContactsRequest {

    private List<String> contacts;
}