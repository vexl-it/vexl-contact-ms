package com.cleevio.vexl.common.integration.firebase.service;

import com.cleevio.vexl.module.push.dto.GroupJoinedNotification;

public interface NotificationService {

    void sendPushNotification(GroupJoinedNotification groupJoined);
}
