package com.cleevio.vexl.module.contact.dto.response;

import lombok.Data;

@Data
public class ImportResponse {

    private final boolean imported;
    private final String message;
}
