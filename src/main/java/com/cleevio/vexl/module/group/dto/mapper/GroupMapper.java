package com.cleevio.vexl.module.group.dto.mapper;

import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.response.GroupsResponse;
import com.cleevio.vexl.module.group.entity.Group;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<GroupsResponse.GroupResponse> mapListToGroupResponse(List<Group> group) {
        return group.stream()
                .map(this::mapSigleToGroupResponse)
                .toList();
    }

    public GroupsResponse.GroupResponse mapSigleToGroupResponse(Group group) {
        return new GroupsResponse.GroupResponse(group);
    }

}
