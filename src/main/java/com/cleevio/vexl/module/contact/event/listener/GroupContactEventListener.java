package com.cleevio.vexl.module.contact.event.listener;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.module.group.event.ImportGroupEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class GroupContactEventListener {

    private final ImportService importService;

    @EventListener
    public void onImportGroupEvent(ImportGroupEvent event) {
        final String groupUuid = event.groupUuid();
        final List<String> groupUuidHashInList = List.of(CLibrary.CRYPTO_LIB.sha256_hash(groupUuid, groupUuid.length()));
        this.importService.importContacts(event.user(), new ImportRequest(groupUuidHashInList));
    }

}
