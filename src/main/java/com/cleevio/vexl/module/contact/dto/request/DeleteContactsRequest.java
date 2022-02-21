package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class DeleteContactsRequest {

    @NotBlank
    @Schema(required = true, description = "Public keys of contacts to delete in Base64 format")
    private List<String> contactsToDelete;
}
