package com.cleevio.vexl.module.contact.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserContactResponse {

    private final Set<byte[]> contactHashes;

    public UserContactResponse(Set<byte[]> contactHashes) {
        this.contactHashes = contactHashes;
    }
}
