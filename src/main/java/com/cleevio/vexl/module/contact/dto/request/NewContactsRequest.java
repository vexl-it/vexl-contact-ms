package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

public record NewContactsRequest(

        @NotNull
        @Schema(required = true, description = "Contacts in String format. Not encrypted.")
        List<String> contacts

) {
}
