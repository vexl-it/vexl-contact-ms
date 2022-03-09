package com.cleevio.vexl.module.user.dto.request;

import com.cleevio.vexl.common.serializer.Base64Deserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull
    @JsonDeserialize(using = Base64Deserializer.class)
    @Schema(required = true, description = "User public key in Base64 format")
    private byte[] publicKey;

    @NotNull
    @JsonDeserialize(using = Base64Deserializer.class)
    @Schema(required = true, description = "Phone hash or FacebookId hash in Base64 format")
    private byte[] hash;

}
