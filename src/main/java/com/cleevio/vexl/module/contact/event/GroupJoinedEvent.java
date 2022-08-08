package com.cleevio.vexl.module.contact.event;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public record GroupJoinedEvent(

        @NotBlank
        String groupUuid,

        Set<@NotBlank String> membersFirebaseTokens

) {
}
