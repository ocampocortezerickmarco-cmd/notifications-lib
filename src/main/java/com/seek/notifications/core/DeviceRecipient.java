package com.seek.notifications.core;

import java.util.Objects;

/**
 * Destinatario para el canal PUSH.
 * Se usa un token de dispositivo (por ejemplo FCM/APNS).
 */
public record DeviceRecipient(String deviceToken) implements Recipient {
    public DeviceRecipient {
        Objects.requireNonNull(deviceToken);
    }
}