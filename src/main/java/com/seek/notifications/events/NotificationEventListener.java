package com.seek.notifications.events;

/**
 * Eventos del ciclo de vida:
 * - Requested: antes del envío
 * - Sent: envío exitoso
 * - Failed: envío fallido
 */
public interface NotificationEventListener {
    void onEvent(NotificationEvent event);
}