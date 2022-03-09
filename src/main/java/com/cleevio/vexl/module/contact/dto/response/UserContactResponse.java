package com.cleevio.vexl.module.contact.dto.response;

import com.cleevio.vexl.common.serializer.Base64Serializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContactResponse {

    @Schema(description = "PublicKey in Base64.")
    @JsonSerialize(using = Base64Serializer.class)
    private byte[] publicKey;

}
