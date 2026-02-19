package com.seek.notifications.models;

import java.time.Instant;

/**
 * Resultado del envío de una notificación.
 * <p>
 * En una integración real, providerMessageId representaría el ID retornado por SendGrid/Twilio/FCM.
 * error contiene información estructurada para diferenciar validación vs errores del proveedor.
 */
public record SendResult(boolean success, String providerName, String providerMessageId, ErrorInfo error,
                         Instant timestamp) {
    public static SendResult ok(String provider, String msgId) {
        return new SendResult(true, provider, msgId, null, Instant.now());
    }

    public static SendResult fail(String provider, ErrorInfo error) {
        return new SendResult(false, provider, null, error, Instant.now());
    }

    public record ErrorInfo(ErrorCode code, String message, Throwable cause) {
    }

    public enum ErrorCode {VALIDATION_ERROR, PROVIDER_ERROR, TRANSIENT_ERROR}
}