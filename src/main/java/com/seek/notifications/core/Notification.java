package com.seek.notifications.core;

import java.time.Instant;
import java.util.Objects;

/**
 * Modelo unificado de una notificación.
 * <p>
 * Contiene:
 * - channel: canal de envío (EMAIL, SMS, PUSH, etc.)
 * - recipient: destinatario tipado (EmailRecipient, PhoneRecipient, DeviceRecipient)
 * - message: payload tipado del canal (EmailMessage, SmsMessage, PushMessage)
 * - metadata: datos de trazabilidad (correlationId, createdAt)
 * <p>
 * Nota: La librería usa tipos específicos por canal para evitar mapas genéricos y errores en runtime.
 */
public record Notification(Channel channel, Recipient recipient, Message message, Metadata metadata) {
    public Notification {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(recipient);
        Objects.requireNonNull(message);
        metadata = (metadata == null) ? Metadata.empty() : metadata;
    }

    public static Builder builder(Channel channel) {
        return new Builder(channel);
    }

    public static final class Builder {
        private final Channel channel;
        private Recipient recipient;
        private Message message;
        private Metadata metadata;

        private Builder(Channel channel) {
            this.channel = channel;
        }

        public Builder recipient(Recipient recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder message(Message message) {
            this.message = message;
            return this;
        }

        public Builder metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Notification build() {
            return new Notification(channel, recipient, message, metadata);
        }
    }

    public record Metadata(String correlationId, Instant createdAt) {
        public static Metadata empty() {
            return new Metadata(null, Instant.now());
        }
    }
}