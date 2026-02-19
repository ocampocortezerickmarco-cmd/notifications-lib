package com.seek.notifications.models;

/**
 * Canales soportados por la librería.
 * <p>
 * Nota: Se usa enum por simplicidad. Si en el futuro se desea evitar modificar este archivo
 * para agregar canales, se puede migrar a un modelo basado en "ChannelId" (String) y un registry.
 */
public enum Channel {EMAIL, SMS, PUSH}
