package com.cleevio.vexl.module.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record CreateGroupRequest(

    @NotBlank
    @Schema(required = true)
    String name,

    @NotBlank
    @Schema(required = true)
    String logo,

    @Schema(required = true, description = "When the group will be deleted. Unix timestamp format.")
    long expiration,

    @Schema(required = true, description = "Since no-one will be able to join the group. Unix timestamp format.")
    long closureAt

) {
}
