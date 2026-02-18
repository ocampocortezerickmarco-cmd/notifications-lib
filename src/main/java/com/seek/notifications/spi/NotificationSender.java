package com.seek.notifications.spi;

import com.seek.notifications.core.Channel;
import com.seek.notifications.core.Notification;
import com.seek.notifications.core.SendResult;

public interface NotificationSender {
    Channel channel();

    String providerName();

    SendResult send(Notification notification);
}
