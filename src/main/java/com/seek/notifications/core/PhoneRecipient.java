package com.seek.notifications.core;

import java.util.Objects;

/**
 * Destinatario para el canal SMS.
 * Se espera formato E.164, ejemplo: +521234567890.
 */
public record PhoneRecipient(String e164Phone) implements Recipient {
    public PhoneRecipient {
        Objects.requireNonNull(e164Phone);
    }
}