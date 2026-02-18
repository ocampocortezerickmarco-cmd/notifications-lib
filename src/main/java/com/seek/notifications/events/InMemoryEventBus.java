package com.seek.notifications.events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementación in-memory del bus de eventos.
 * No requiere infraestructura externa y es suficiente para la prueba técnica.
 */
public final class InMemoryEventBus implements EventBus {

    private final CopyOnWriteArrayList<NotificationEventListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void publish(NotificationEvent event) {
        for (var l : listeners) l.onEvent(event);
    }

    @Override
    public void subscribe(NotificationEventListener listener) {
        listeners.add(listener);
    }
}