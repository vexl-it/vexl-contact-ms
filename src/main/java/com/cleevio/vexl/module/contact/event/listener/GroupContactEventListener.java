package com.cleevio.vexl.module.contact.event.listener;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.module.group.event.GroupImportedEvent;
import com.cleevio.vexl.module.group.event.GroupJoinedEvent;
import com.cleevio.vexl.module.group.event.GroupLeftEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Component
@Validated
@RequiredArgsConstructor
class GroupContactEventListener {

    private final ImportService importService;
    private final ContactService contactService;

    @EventListener
    public void onGroupImportedEvent(@Valid final GroupImportedEvent event) {
        final String groupUuid = event.groupUuid();
        final List<String> groupUuidHashInList = List.of(CLibrary.CRYPTO_LIB.sha256_hash(groupUuid, groupUuid.length()));
        this.importService.importContacts(event.user(), new ImportRequest(groupUuidHashInList));
    }

    @EventListener
    public void onGroupJoinedEvent(@Valid final GroupJoinedEvent event) {
        this.importService.importContacts(event.user(), new ImportRequest(List.of(event.groupUuid())));
    }

    @EventListener
    public void onGroupLeftEvent(@Valid final GroupLeftEvent event) {
        this.contactService.deleteContactByHash(event.hash(), event.groupUuidHash());
    }

}
