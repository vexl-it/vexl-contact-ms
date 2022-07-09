package com.cleevio.vexl.module.group.dto.response;

import com.cleevio.vexl.module.group.entity.Group;
import org.springframework.lang.Nullable;

import java.util.List;

public record GroupsResponse(

        List<GroupResponse> groupResponse

) {

    public record GroupResponse(

            String uuid,

            String name,

            @Nullable
            String logoUrl,

            long createdAt,

            long expirationAt,

            long closureAt,

            int code

    ) {

        public GroupResponse(Group group) {
            this(
                    group.getUuid(),
                    group.getName(),
                    group.getLogoUrl(),
                    group.getCreatedAt(),
                    group.getExpirationAt(),
                    group.getClosureAt(),
                    group.getCode()
            );
        }
    }
}
