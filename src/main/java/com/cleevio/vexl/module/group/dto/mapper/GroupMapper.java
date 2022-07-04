package com.cleevio.vexl.module.group.dto.mapper;

import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public Group mapSingleToGroup(CreateGroupRequest request) {
        return Group.builder()
                .name(request.name())
                .logoUrl(request.logo())
                .expirationAt(request.expiration())
                .closureAt(request.closureAt())
                .build();
    }

}
