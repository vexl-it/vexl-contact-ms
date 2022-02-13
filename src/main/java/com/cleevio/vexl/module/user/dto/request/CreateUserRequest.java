package com.cleevio.vexl.module.user.dto.request;

import lombok.Data;

@Data
public class CreateUserRequest {

    private final byte[] publicKey;
    private final byte[] hash;

}
