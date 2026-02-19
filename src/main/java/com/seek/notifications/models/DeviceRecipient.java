package com.seek.notifications.models;

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