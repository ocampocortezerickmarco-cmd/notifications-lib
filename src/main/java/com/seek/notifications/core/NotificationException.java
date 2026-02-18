package com.seek.notifications.core;

/**
 * Excepción base de la librería.
 * Se usa para señalar problemas de uso/configuración de la librería.
 */
public class NotificationException extends RuntimeException {
    public NotificationException(String msg) {
        super(msg);
    }

    public NotificationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}