package com.cleevio.vexl.util;

import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
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

}
