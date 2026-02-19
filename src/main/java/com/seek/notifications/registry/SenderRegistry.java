package com.seek.notifications.registry;

import com.seek.notifications.exceptions.NotificationException;
import com.seek.notifications.models.Channel;
import com.seek.notifications.spi.NotificationSender;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registro de senders por canal.
 * <p>
 * Permite mapear un Channel -> NotificationSender, evitando if/switch en el cliente.
 * Agregar o cambiar un provider no requiere modificar NotificationClient.
 */
public final class SenderRegistry {
    private final Map<Channel, NotificationSender> byChannel = new EnumMap<>(Channel.class);

    public SenderRegistry register(NotificationSender sender) {
        Objects.requireNonNull(sender);
        byChannel.put(sender.channel(), sender);
        return this;
    }

    public NotificationSender get(Channel channel) {
        NotificationSender sender = byChannel.get(channel);
        if (sender == null) throw new NotificationException("No sender registered for channel: " + channel);
        return sender;
    }
}