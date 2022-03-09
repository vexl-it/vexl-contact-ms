package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.common.serializer.Base64Serializer;
import com.cleevio.vexl.module.user.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResponse {

    private final Long id;

    @Schema(description = "PublicKey in Base64.")
    @JsonSerialize(using = Base64Serializer.class)
    private final byte[] publicKey;

    @Schema(description = "Phone hash or FacebookId hash in Base64.")
    @JsonSerialize(using = Base64Serializer.class)
    private final byte[] hash;

    public UserResponse(User user) {
        this.id = user.getId();
        this.publicKey = user.getPublicKey();
        this.hash = user.getHash();
    }
}
