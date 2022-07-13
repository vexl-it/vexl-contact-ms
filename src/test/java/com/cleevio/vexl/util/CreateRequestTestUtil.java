package com.cleevio.vexl.util;

import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateRequestTestUtil {

    public static CreateGroupRequest createCreateGroupRequest() {
        return new CreateGroupRequest(
                "dummy_name",
                "dummy_logo",
                9223372036854777L,
                654654648
        );
    }

    public static CreateGroupRequest createCreateGroupRequestExpired() {
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

    public static ImportRequest createImportRequest(List<String> contacts) {
        return new ImportRequest(
                contacts
        );
    }

    public static DeleteContactsRequest createDeleteContactsRequest(List<String> contactsToDelete) {
        return new DeleteContactsRequest(
                contactsToDelete
        );
    }

}
