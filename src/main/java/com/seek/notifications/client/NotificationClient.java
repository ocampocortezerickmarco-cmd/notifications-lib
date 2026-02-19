package com.seek.notifications.client;

import com.seek.notifications.exceptions.SendFailedException;
import com.seek.notifications.registry.SenderRegistry;
import com.seek.notifications.exceptions.ValidationException;
import com.seek.notifications.events.EventBus;
import com.seek.notifications.events.NotificationEvent;
import com.seek.notifications.models.Notification;
import com.seek.notifications.models.SendResult;
import com.seek.notifications.spi.NotificationSender;
import com.seek.notifications.spi.NotificationValidator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;


/**
 * Facade principal de la librería.
 * <p>
 * - Expone una API unificada (send / sendAsync) para enviar notificaciones sin importar el canal.
 * - Selecciona el sender adecuado a través de un registro (SenderRegistry).
 * - Valida la notificación antes de enviarla (NotificationValidator).
 * - Opcionalmente publica eventos de ciclo de vida (Requested/Sent/Failed) mediante EventBus.
 * <p>
 * Diseño:
 * - Strategy: cada provider implementa NotificationSender.
 * - Builder: configuración por código (sin YAML/properties).
 * - DIP: depende de interfaces (NotificationSender, NotificationValidator, EventBus).
 */
public final class NotificationClient {

    private final SenderRegistry registry;
    private final NotificationValidator validator;
    private final Executor executor;
    private final EventBus eventBus;

    private NotificationClient(SenderRegistry registry, NotificationValidator validator, Executor executor, EventBus eventBus) {
        this.registry = registry;
        this.validator = validator;
        this.executor = executor;
        this.eventBus = eventBus;
    }

    public SendResult send(Notification notification) {
        Objects.requireNonNull(notification);

        // 1) Validación: fallas por input inválido deben ser claras y no reintentables.
        validator.validate(notification);

        // 2) Selección del sender por canal: desacopla el cliente del proveedor.
        NotificationSender sender = registry.get(notification.channel());

        // 3) Publicación de eventos (opcional) para auditoría/métricas sin acoplar lógica.
        String corr = notification.metadata().correlationId();
        if (corr == null || corr.isBlank()) corr = "corr-" + UUID.randomUUID();

        publish(new NotificationEvent.Requested(corr, notification.channel(), Instant.now(), notification));

        // 4) enviar
        try {
            SendResult result = sender.send(notification);
            publish(new NotificationEvent.Sent(corr, notification.channel(), Instant.now(), result));
            return result;
        } catch (ValidationException e) {
            publish(new NotificationEvent.Failed(corr, notification.channel(), Instant.now(), e));
            throw e;
        } catch (Exception e) {
            publish(new NotificationEvent.Failed(corr, notification.channel(), Instant.now(), e));
            throw new SendFailedException("Send failed via provider=" + sender.providerName(), e);
        }
    }

    public CompletableFuture<SendResult> sendAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> send(notification), executor);
    }

    private void publish(NotificationEvent event) {
        if (eventBus != null) eventBus.publish(event);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final SenderRegistry registry = new SenderRegistry();
        private NotificationValidator validator;
        private Executor executor;
        private EventBus eventBus;

        public Builder register(NotificationSender sender) {
            registry.register(sender);
            return this;
        }

        public Builder validator(NotificationValidator validator) {
            this.validator = validator;
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder eventBus(EventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }

        public NotificationClient build() {
            NotificationValidator v = (validator != null) ? validator : new com.seek.notifications.validation.DefaultValidator();
            Executor ex = (executor != null) ? executor : ForkJoinPool.commonPool();
            return new NotificationClient(registry, v, ex, eventBus);
        }
    }
}