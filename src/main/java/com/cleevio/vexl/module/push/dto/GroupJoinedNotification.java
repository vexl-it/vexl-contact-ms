package com.cleevio.vexl.module.push.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public record GroupJoinedNotification(

        @NotBlank
        String event,

        @NotBlank
        String groupUuid,

        List<@NotBlank String> membersFirebaseTokens
) {
}
