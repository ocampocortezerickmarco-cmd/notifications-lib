package com.seek.notifications.spi;

import com.seek.notifications.core.Notification;

public interface NotificationValidator {
    void validate(Notification notification);
}