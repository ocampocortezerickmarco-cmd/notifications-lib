package com.seek.notifications.events;

import com.seek.notifications.models.Channel;
import com.seek.notifications.models.Notification;
import com.seek.notifications.models.SendResult;

import java.time.Instant;

/**
 * Eventos del ciclo de vida:
 * - Requested: antes del envío
 * - Sent: envío exitoso
 * - Failed: envío fallido
 */
public sealed interface NotificationEvent permits NotificationEvent.Requested, NotificationEvent.Sent, NotificationEvent.Failed {

    String correlationId();

    Channel channel();

    Instant at();

    record Requested(String correlationId, Channel channel, Instant at,
                     Notification notification) implements NotificationEvent {
    }

    record Sent(String correlationId, Channel channel, Instant at, SendResult result) implements NotificationEvent {
    }

    record Failed(String correlationId, Channel channel, Instant at, Throwable error) implements NotificationEvent {
    }
}
