package com.seek.notifications.providers.sms;

import com.seek.notifications.core.Channel;
import com.seek.notifications.core.Notification;
import com.seek.notifications.core.SendResult;
import com.seek.notifications.spi.NotificationSender;

import java.util.UUID;

public final class TwilioSmsSender implements NotificationSender {
    private final String accountSid;
    private final String authToken;

    public TwilioSmsSender(String accountSid, String authToken) {
        this.accountSid = accountSid;
        this.authToken = authToken;
    }

    @Override
    public Channel channel() {
        return Channel.SMS;
    }

    @Override
    public String providerName() {
        return "twilio";
    }

    @Override
    public SendResult send(Notification n) {
        return SendResult.ok(providerName(), "tw-" + UUID.randomUUID());
    }
}