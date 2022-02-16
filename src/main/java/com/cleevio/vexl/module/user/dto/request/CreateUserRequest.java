package com.cleevio.vexl.module.user.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateUserRequest {

    @NotBlank
    private final String publicKey;
    @NotBlank
    private final String hash;

}
