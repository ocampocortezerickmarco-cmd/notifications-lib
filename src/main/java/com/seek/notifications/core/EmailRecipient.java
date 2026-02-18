package com.seek.notifications.core;

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