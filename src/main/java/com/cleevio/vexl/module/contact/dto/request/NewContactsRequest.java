package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewContactsRequest {

    @NotNull
    @Schema(required = true, description = "Contacts in String format. Not encrypted.")
    private List<String> contacts;
}
