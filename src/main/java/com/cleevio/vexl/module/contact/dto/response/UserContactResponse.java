package com.cleevio.vexl.module.contact.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserContactResponse {

    @Schema(description = "PublicKeys in Base64")
    private final String publicKeys;

}
