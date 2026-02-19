package com.seek.notifications.examples;

import com.seek.notifications.client.NotificationClient;
import com.seek.notifications.events.InMemoryEventBus;
import com.seek.notifications.models.*;
import com.seek.notifications.providers.email.SendGridEmailSender;
import com.seek.notifications.providers.push.FcmPushSender;
import com.seek.notifications.providers.sms.TwilioSmsSender;
import com.seek.notifications.retry.RetryingSender;

import java.time.Duration;
import java.time.Instant;

public class NotificationExamples {

    public static void main(String[] args) {
        var bus = new InMemoryEventBus();
        bus.subscribe(event -> System.out.println("EVENT => " + event));

        NotificationClient client = NotificationClient.builder().eventBus(bus).register(new RetryingSender(new SendGridEmailSender("SG_API_KEY"), 3, Duration.ofMillis(30))).register(new TwilioSmsSender("SID", "TOKEN")).register(new FcmPushSender("{service-account-json}")).build();

        Notification emailSync = Notification.builder(Channel.EMAIL).recipient(new EmailRecipient("user@mail.com")).message(new EmailMessage("Bienvenido", "Gracias por registrarte")).metadata(new Notification.Metadata("corr-demo-sync-001", Instant.now())).build();

        Notification emailAsync = Notification.builder(Channel.EMAIL).recipient(new EmailRecipient("user@mail.com")).message(new EmailMessage("Bienvenido", "Gracias por registrarte")).metadata(new Notification.Metadata("corr-demo-async-001", Instant.now())).build();

        Notification sms = Notification.builder(Channel.SMS).recipient(new PhoneRecipient("+521234567890")).message(new SmsMessage("Hola desde SMS")).metadata(new Notification.Metadata("corr-demo-sms-001", Instant.now())).build();

        Notification push = Notification.builder(Channel.PUSH).recipient(new DeviceRecipient("device-token-123")).message(new PushMessage("Alerta", "Tienes una notificación")).metadata(new Notification.Metadata("corr-demo-push-001", Instant.now())).build();

        System.out.println("SYNC => " + client.send(emailSync));
        System.out.println("ASYNC => " + client.sendAsync(emailAsync).join());
        System.out.println("SMS => " + client.send(sms));
        System.out.println("PUSH => " + client.send(push));
    }
}
