package com.seek.notifications.models;

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