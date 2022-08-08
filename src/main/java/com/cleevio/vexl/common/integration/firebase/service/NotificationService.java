package com.cleevio.vexl.common.integration.firebase.service;

import com.cleevio.vexl.module.push.dto.PushNotification;

public interface NotificationService {

    void sendPushNotification(PushNotification groupJoined);
}
