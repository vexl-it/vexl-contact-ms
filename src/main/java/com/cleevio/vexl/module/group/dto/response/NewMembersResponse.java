package com.cleevio.vexl.module.group.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record NewMembersResponse(

        List<GroupMembers> newMembers

) {

    public record GroupMembers(

            String groupUuid,

            List<String> publicKeys

    ) {
    }

    public NewMembersResponse(Map<String, List<String>> newMembers) {
        this(createGroupMembers(newMembers));
    }

    private static List<GroupMembers> createGroupMembers(Map<String, List<String>> newMembers) {
        List<GroupMembers> newMembersResponse = new ArrayList<>();
        newMembers.forEach((k, v) -> newMembersResponse.add(new GroupMembers(k, v)));
        return newMembersResponse;
    }
}
