package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.common.integration.firebase.service.NotificationService;
import com.cleevio.vexl.module.contact.event.GroupJoinedEvent;
import com.cleevio.vexl.module.push.constant.NotificationType;
import com.cleevio.vexl.module.push.dto.PushNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PushService {
    private final NotificationService notificationService;

    public void sendGroupJoinedNotification(GroupJoinedEvent event) {
        this.notificationService.sendPushNotification(new PushNotification(NotificationType.GROUP_NEW_MEMBER, event.groupUuid(), event.membersFirebaseTokens()));
    }

    public void sendImportedNotification(Set<String> firebaseTokens) {
        this.notificationService.sendPushNotification(new PushNotification(NotificationType.NEW_APP_USER, null, firebaseTokens));
    }
}
