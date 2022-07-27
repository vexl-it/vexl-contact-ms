package com.cleevio.vexl.module.contact.event;

import javax.validation.constraints.NotBlank;
import java.util.List;

public record GroupJoinedEvent(

        @NotBlank
        String groupUuid,

        List<@NotBlank String> membersFirebaseTokens

) {
}
