package com.cleevio.vexl.module.user.dto.response;

import com.cleevio.vexl.module.user.entity.User;
import lombok.Data;

@Data
public class UserResponse {

    private final Long id;
    private final byte[] publicKey;
    private final byte[] hash;

    public UserResponse(User user) {
        this.id = user.getId();
        this.publicKey = user.getPublicKey();
        this.hash = user.getHash();
    }
}
