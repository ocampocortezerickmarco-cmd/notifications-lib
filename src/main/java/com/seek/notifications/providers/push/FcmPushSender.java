package com.seek.notifications.providers.push;

import com.seek.notifications.core.Channel;
import com.seek.notifications.core.Notification;
import com.seek.notifications.core.SendResult;
import com.seek.notifications.spi.NotificationSender;

import java.util.UUID;

public final class FcmPushSender implements NotificationSender {
    private final String serviceAccountJson;

    public FcmPushSender(String serviceAccountJson) {
        this.serviceAccountJson = serviceAccountJson;
    }

    @Override
    public Channel channel() {
        return Channel.PUSH;
    }

    @Override
    public String providerName() {
        return "fcm";
    }

    @Override
    public SendResult send(Notification n) {
        return SendResult.ok(providerName(), "fcm-" + UUID.randomUUID());
    }
}