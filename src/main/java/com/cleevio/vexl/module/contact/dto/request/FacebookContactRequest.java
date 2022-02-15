package com.cleevio.vexl.module.contact.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FacebookContactRequest {

    @NotBlank
    private final String accessToken;
    @NotBlank
    private final String facebookId;
}
