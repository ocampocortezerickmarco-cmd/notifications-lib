package com.seek.notifications.spi;

import com.seek.notifications.models.Channel;
import com.seek.notifications.models.Notification;
import com.seek.notifications.models.SendResult;

public interface NotificationSender {
    Channel channel();

    String providerName();

    SendResult send(Notification notification);
}
