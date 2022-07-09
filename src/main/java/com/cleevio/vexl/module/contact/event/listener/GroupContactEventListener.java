package com.cleevio.vexl.module.contact.event.listener;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.module.group.event.ImportGroupEvent;
import com.cleevio.vexl.module.group.event.LeaveGroupEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class GroupContactEventListener {

    private final ImportService importService;
    private final ContactService contactService;

    @EventListener
    public void onImportGroupEvent(final ImportGroupEvent event) {
        final String groupUuid = event.groupUuid();
        final List<String> groupUuidHashInList = List.of(CLibrary.CRYPTO_LIB.sha256_hash(groupUuid, groupUuid.length()));
        this.importService.importContacts(event.user(), new ImportRequest(groupUuidHashInList));
    }

    @EventListener
    public void onLeaveGroupEvent(final LeaveGroupEvent event) {
        this.contactService.deleteContactByHash(event.hash(), event.groupUuidHash());
    }

}
