package com.seek.notifications.events;

/**
 * EventBus (Pub/Sub) opcional para publicar eventos del ciclo de vida del envío.
 * Permite auditoría, métricas y logging sin acoplar esas responsabilidades al core.
 */
public interface EventBus {
    void publish(NotificationEvent event);

    void subscribe(NotificationEventListener listener);
}