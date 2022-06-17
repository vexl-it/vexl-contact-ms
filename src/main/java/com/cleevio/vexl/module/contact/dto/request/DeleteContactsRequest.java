package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

public record DeleteContactsRequest(

        @NotNull
        @Schema(required = true, description = "Public keys of contacts to delete.")
        List<String> contactsToDelete

) {
}
