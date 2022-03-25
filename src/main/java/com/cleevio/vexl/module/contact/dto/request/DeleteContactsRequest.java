package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeleteContactsRequest {

    @NotNull
    @Schema(required = true, description = "Public keys of contacts to delete in Base64 format")
    private List<String> contactsToDelete;
}
