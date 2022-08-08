package com.cleevio.vexl.common.integration.firebase.service;

import com.cleevio.vexl.module.push.dto.PushNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseService implements NotificationService {

    private static final String GROUP_UUID = "group_uuid";
    private static final String TYPE = "type";

    public void sendPushNotification(final PushNotification push) {
        push.membersFirebaseTokens().forEach(m -> {
            processNotification(m, push);
        });
    }

    private void processNotification(String firebaseToken, PushNotification push) {
        try {
            var messageBuilder = Message.builder();

            messageBuilder.setNotification(Notification.builder().setTitle(push.type().name()).setBody(push.type().name()).build());
            messageBuilder.setToken(firebaseToken);
            if (push.groupUuid() != null) {
                messageBuilder.putData(GROUP_UUID, push.groupUuid());
            }
            messageBuilder.putData(TYPE, push.type().name());

            final String response = FirebaseMessaging.getInstance().sendAsync(messageBuilder.build()).get();
            log.info("Sent message: " + response);

        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }
}
