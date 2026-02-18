package com.seek.notifications.core;

import java.util.Objects;

/**
 * Mensaje para el canal EMAIL.
 * subject: asunto del correo
 * body: contenido del correo
 */
public record EmailMessage(String subject, String body) implements Message {
    public EmailMessage {
        Objects.requireNonNull(subject);
        Objects.requireNonNull(body);
    }
}