package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.List;

public record DeleteContactsRequest(

        @Schema(required = true, description = "Public keys of contacts to delete.")
        List<@NotBlank String> contactsToDelete

) {
}
