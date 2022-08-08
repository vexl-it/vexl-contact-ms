package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.common.integration.firebase.service.NotificationService;
import com.cleevio.vexl.module.contact.event.GroupJoinedEvent;
import com.cleevio.vexl.module.push.constant.NotificationType;
import com.cleevio.vexl.module.push.dto.PushNotification;
import com.cleevio.vexl.module.push.entity.Push;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PushService {
    private final NotificationService notificationService;
    private final PushRepository pushRepository;

    public void sendImportedNotification(Set<String> firebaseTokens) {
        this.notificationService.sendPushNotification(new PushNotification(NotificationType.NEW_APP_USER, null, firebaseTokens));
    }

    @Transactional
    public void saveNotification(GroupJoinedEvent event) {
        this.pushRepository.save(new Push(event.groupUuid(), event.membersFirebaseTokens().toArray(new String[0])));
    }

    @Transactional
    public Map<String, Set<String>> processPushNotification() {
        final List<Push> pushes = pushRepository.findAllPushNotificationsWithExistingGroup();
        if (pushes.isEmpty()) new HashMap<>();

        final Map<String, Set<String>> notifications = new HashMap<>();
        pushes.forEach(push -> {
            if (notifications.containsKey(push.getGroupUuid())) {
                notifications.get(push.getGroupUuid()).addAll(Arrays.asList(push.getFirebaseTokens()));
            } else {
                notifications.put(push.getGroupUuid(), new HashSet<>(Arrays.asList(push.getFirebaseTokens())));
            }
        });

        sendNewGroupMemberNotification(notifications);
        clearPushNotifications(pushes);
        return notifications;
    }

    private void clearPushNotifications(final List<Push> pushes) {
        this.pushRepository.deleteAllInBatch(pushes);
        this.pushRepository.deleteOrphans();
    }

    private void sendNewGroupMemberNotification(Map<String, Set<String>> notifications) {
        notifications.forEach((k, v) -> this.notificationService.sendPushNotification(new PushNotification(NotificationType.GROUP_NEW_MEMBER, k, v)));
    }
}
