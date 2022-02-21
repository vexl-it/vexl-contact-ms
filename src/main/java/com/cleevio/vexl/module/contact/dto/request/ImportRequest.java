package com.cleevio.vexl.module.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ImportRequest {

    @NotBlank
    @Schema(required = true, description = "Contacts in String. Will be hashed with HMAC-SHA256 on BE.")
    private List<String> contacts;

}
