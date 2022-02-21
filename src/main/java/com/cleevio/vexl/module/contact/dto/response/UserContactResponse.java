package com.cleevio.vexl.module.contact.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserContactResponse {

    private final Set<String> publicKeys;

    public UserContactResponse(Set<String> publicKeys) {
        this.publicKeys = publicKeys;
    }
}
