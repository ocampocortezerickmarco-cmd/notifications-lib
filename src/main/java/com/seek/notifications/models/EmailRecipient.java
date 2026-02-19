package com.seek.notifications.models;

import java.util.Objects;

/**
 * Destinatario para el canal EMAIL.
 * Se valida formato de correo en el validator.
 */

public record EmailRecipient(String email) implements Recipient {
    public EmailRecipient {
        Objects.requireNonNull(email);
    }
}