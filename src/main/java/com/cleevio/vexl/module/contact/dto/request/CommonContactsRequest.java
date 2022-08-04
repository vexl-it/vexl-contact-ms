package com.cleevio.vexl.module.contact.dto.request;

import javax.validation.constraints.NotBlank;
import java.util.List;

public record CommonContactsRequest(

    List<@NotBlank String> publicKeys

) {
}
