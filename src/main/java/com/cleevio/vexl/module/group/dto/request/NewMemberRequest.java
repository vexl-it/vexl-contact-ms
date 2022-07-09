package com.cleevio.vexl.module.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

public record NewMemberRequest(

        @NotNull
        @Schema(required = true, description = "UUIDs of the groups you are interested in.")
        List<String> groupUuids,

        @NotNull
        @Schema(required = true, description = "Public keys of contacts you already have.")
        List<String> publicKeys


) {
}
