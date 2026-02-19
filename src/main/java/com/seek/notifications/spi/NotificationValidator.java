package com.seek.notifications.spi;

import com.seek.notifications.models.Notification;

public interface NotificationValidator {
    void validate(Notification notification);
}