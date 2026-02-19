package com.seek.notifications.models;

public sealed interface Recipient permits EmailRecipient, PhoneRecipient, DeviceRecipient {}
