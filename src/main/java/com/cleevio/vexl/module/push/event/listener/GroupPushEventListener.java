package com.cleevio.vexl.module.push.event.listener;

import com.cleevio.vexl.module.contact.event.GroupJoinedEvent;
import com.cleevio.vexl.module.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Component
@Validated
@RequiredArgsConstructor
class GroupPushEventListener {

    private final PushService pushService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGroupJoinedEvent(@Valid final GroupJoinedEvent event) {
        pushService.sendGroupJoinedNotification(event);
    }

}