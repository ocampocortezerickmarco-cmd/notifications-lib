package com.seek.notifications.core;

/**
 * Error por datos inválidos (input).
 * No es reintentable: debe corregirse el destinatario o el mensaje.
 */
public class ValidationException extends NotificationException {
    public ValidationException(String msg) {
        super(msg);
    }
}