package com.cleevio.vexl.module.group.dto.response;

import com.cleevio.vexl.module.group.entity.Group;

public record GroupCreatedResponse(

        String uuid,

        String name,

        long expiration,

        long closure

) {

    public GroupCreatedResponse(Group group) {
        this(
                group.getUuid(),
                group.getName(),
                group.getExpirationAt(),
                group.getClosureAt()
        );
    }
}
