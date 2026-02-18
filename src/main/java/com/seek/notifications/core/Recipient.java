package com.seek.notifications.core;

public sealed interface Recipient permits EmailRecipient, PhoneRecipient, DeviceRecipient {}
