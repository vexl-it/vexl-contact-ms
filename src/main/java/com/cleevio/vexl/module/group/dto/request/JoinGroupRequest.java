package com.cleevio.vexl.module.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record JoinGroupRequest (

    @NotBlank
    @Schema(required = true, description = "Group UUID to join to.")
    String groupUuid

) {
}
