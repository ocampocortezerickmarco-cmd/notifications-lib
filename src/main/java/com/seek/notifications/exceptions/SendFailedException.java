package com.seek.notifications.exceptions;

/**
 * Error durante el envío (fallas del provider, timeouts simulados, etc.)
 * Puede ser reintentable dependiendo del caso.
 */
public class SendFailedException extends NotificationException {
    public SendFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}