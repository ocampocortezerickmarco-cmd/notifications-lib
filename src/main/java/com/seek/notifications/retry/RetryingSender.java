package com.seek.notifications.retry;

import com.seek.notifications.core.*;
import com.seek.notifications.spi.NotificationSender;

import java.time.Duration;

/**
 * Decorator de reintentos.
 * <p>
 * Envuelve un NotificationSender y reintenta el envío ante errores transitorios.
 * No reintenta ValidationException (porque es error del input).
 * <p>
 * Patrón: Decorator (añade comportamiento sin modificar el sender original).
 */

public final class RetryingSender implements NotificationSender {

    private final NotificationSender delegate;
    private final int maxAttempts;
    private final Duration backoff;

    public RetryingSender(NotificationSender delegate, int maxAttempts, Duration backoff) {
        this.delegate = delegate;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.backoff = (backoff == null) ? Duration.ZERO : backoff;
    }

    @Override
    public Channel channel() {
        return delegate.channel();
    }

    @Override
    public String providerName() {
        return delegate.providerName();
    }

    @Override
    public SendResult send(Notification notification) {
        RuntimeException last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return delegate.send(notification);
            } catch (ValidationException e) {
                throw e;
            } catch (RuntimeException e) {
                last = e;
                if (attempt < maxAttempts && !backoff.isZero()) {
                    try {
                        Thread.sleep(backoff.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SendFailedException("Retry interrupted", ie);
                    }
                }
            }
        }
        throw new SendFailedException("Send failed after " + maxAttempts + " attempts", last);
    }
}
