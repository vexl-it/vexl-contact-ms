package com.cleevio.vexl.module.contact.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeleteContactsRequest {

    private List<String> contactsToDelete = new ArrayList<>();
}
