package com.cleevio.vexl.module.group.dto.response;

import java.util.List;
import java.util.Map;

public record NewMembersResponse(

        Map<String, List<String>> newMembers

) {
}
