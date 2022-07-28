package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.common.integration.firebase.service.NotificationService;
import com.cleevio.vexl.module.contact.event.GroupJoinedEvent;
import com.cleevio.vexl.module.push.dto.GroupJoinedNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushService {

    private final static String JOINED_GROUP_EVENT = "New member in a group";
    private final NotificationService notificationService;

    public void sendGroupJoinedNotification(GroupJoinedEvent event) {
        this.notificationService.sendPushNotification(new GroupJoinedNotification(JOINED_GROUP_EVENT, event.groupUuid(), event.membersFirebaseTokens()));
    }
}
