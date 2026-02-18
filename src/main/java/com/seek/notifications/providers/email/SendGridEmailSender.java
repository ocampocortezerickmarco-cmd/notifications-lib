package com.seek.notifications.providers.email;


import com.seek.notifications.core.Channel;
import com.seek.notifications.core.Notification;
import com.seek.notifications.core.SendResult;
import com.seek.notifications.spi.NotificationSender;

import java.util.UUID;

public final class SendGridEmailSender implements NotificationSender {
    private final String apiKey;

    public SendGridEmailSender(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public String providerName() {
        return "sendgrid";
    }

    @Override
    public SendResult send(Notification n) {
        return SendResult.ok(providerName(), "sg-" + UUID.randomUUID());
    }
}