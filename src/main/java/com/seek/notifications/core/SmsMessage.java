package com.seek.notifications.core;

import java.util.Objects;

/**
 * Mensaje para el canal SMS.
 * text: contenido plano (sin subject).
 */
public record SmsMessage(String text) implements Message {
    public SmsMessage {
        Objects.requireNonNull(text);
    }
}