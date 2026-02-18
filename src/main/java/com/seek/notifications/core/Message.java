package com.seek.notifications.core;

public sealed interface Message permits EmailMessage, SmsMessage, PushMessage {}
