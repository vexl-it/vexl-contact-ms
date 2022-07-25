package com.cleevio.vexl.module.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public record CreateGroupRequest(

        @NotBlank
        @Schema(required = true)
        String name,

        @NotBlank
        @Schema(required = true)
        String logo,

        @Positive
        @Schema(required = true, description = "When the group will be deleted. Unix timestamp seconds format.")
        long expiration,

        @Positive
        @Schema(required = true, description = "Since no-one will be able to join the group. Unix timestamp seconds format.")
        long closureAt

) {
}
