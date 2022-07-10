package com.cleevio.vexl.util;

import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateRequestTestUtil {

    public static CreateGroupRequest createCreateGroupRequest() {
        return new CreateGroupRequest(
                "dummy_name",
                "dummy_logo",
                65465465,
                654654648
        );
    }

    public static JoinGroupRequest createJoinGroupRequest(int groupUuid) {
        return new JoinGroupRequest(
                groupUuid
        );
    }

}
