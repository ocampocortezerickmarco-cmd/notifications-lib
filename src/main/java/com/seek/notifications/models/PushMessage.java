package com.seek.notifications.models;

import java.util.Objects;

/**
 * Mensaje para el canal PUSH.
 * title: título visible
 * body: cuerpo del push
 */
public record PushMessage(String title, String body) implements Message {
    public PushMessage {
        Objects.requireNonNull(title);
        Objects.requireNonNull(body);
    }
}