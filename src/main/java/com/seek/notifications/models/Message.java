package com.seek.notifications.models;

public sealed interface Message permits EmailMessage, SmsMessage, PushMessage {}
