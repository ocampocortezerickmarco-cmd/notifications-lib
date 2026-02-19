package com.seek.notifications;

import com.seek.notifications.client.NotificationClient;
import com.seek.notifications.events.InMemoryEventBus;
import com.seek.notifications.events.NotificationEvent;
import com.seek.notifications.exceptions.NotificationException;
import com.seek.notifications.models.*;
import com.seek.notifications.spi.NotificationSender;
import com.seek.notifications.spi.NotificationValidator;
import com.seek.notifications.retry.RetryingSender;
import com.seek.notifications.exceptions.SendFailedException;
import com.seek.notifications.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationClientTest {

    static final class FakeEmailSender implements NotificationSender {
        int calls = 0;

        @Override
        public Channel channel() {
            return Channel.EMAIL;
        }

        @Override
        public String providerName() {
            return "fake-email";
        }

        @Override
        public SendResult send(Notification notification) {
            calls++;
            return SendResult.ok(providerName(), "id-1");
        }
    }

    static final class AlwaysFailSender implements NotificationSender {
        int calls = 0;

        @Override
        public Channel channel() {
            return Channel.EMAIL;
        }

        @Override
        public String providerName() {
            return "always-fail";
        }

        @Override
        public SendResult send(Notification notification) {
            calls++;
            throw new RuntimeException("provider down");
        }
    }

    static final class FlakySender implements NotificationSender {
        int calls = 0;
        private final int failTimes;

        FlakySender(int failTimes) {
            this.failTimes = failTimes;
        }

        @Override
        public Channel channel() {
            return Channel.EMAIL;
        }

        @Override
        public String providerName() {
            return "flaky";
        }

        @Override
        public SendResult send(Notification notification) {
            calls++;
            if (calls <= failTimes) throw new RuntimeException("transient error");
            return SendResult.ok(providerName(), "ok-after-retry");
        }
    }

    static final class NoopValidator implements NotificationValidator {
        int calls = 0;

        @Override
        public void validate(Notification notification) {
            calls++;
        }
    }

    static final class AlwaysInvalidValidator implements NotificationValidator {
        @Override
        public void validate(Notification notification) {
            throw new ValidationException("invalid input");
        }
    }

    private static Notification emailNotificationNoMetadata() {
        return Notification.builder(Channel.EMAIL).recipient(new EmailRecipient("a@b.com")).message(new EmailMessage("s", "b")).build();
    }

    @Test
    void send_uses_sender_and_publishes_events() {
        FakeEmailSender sender = new FakeEmailSender();
        NoopValidator validator = new NoopValidator();

        InMemoryEventBus bus = new InMemoryEventBus();
        List<NotificationEvent> events = new ArrayList<>();
        bus.subscribe(events::add);

        NotificationClient client = NotificationClient.builder().eventBus(bus).register(sender).validator(validator).build();

        SendResult r = client.send(emailNotificationNoMetadata());

        assertTrue(r.success());
        assertEquals(1, sender.calls);
        assertEquals(1, validator.calls);
        assertEquals(2, events.size()); // Requested + Sent
        assertTrue(events.get(0) instanceof NotificationEvent.Requested);
        assertTrue(events.get(1) instanceof NotificationEvent.Sent);
    }

    @Test
    void send_async_completes_and_publishes_events() throws Exception {
        FakeEmailSender sender = new FakeEmailSender();
        NoopValidator validator = new NoopValidator();

        InMemoryEventBus bus = new InMemoryEventBus();
        List<NotificationEvent> events = new ArrayList<>();
        bus.subscribe(events::add);

        NotificationClient client = NotificationClient.builder().eventBus(bus).register(sender).validator(validator).build();

        SendResult r = client.sendAsync(emailNotificationNoMetadata()).get();

        assertTrue(r.success());
        assertEquals(1, sender.calls);
        assertEquals(1, validator.calls);
        assertEquals(2, events.size()); // Requested + Sent
    }

    @Test
    void throws_when_sender_missing() {
        NotificationClient client = NotificationClient.builder().build();

        Notification sms = Notification.builder(Channel.SMS).recipient(new PhoneRecipient("+521234567890")).message(new SmsMessage("hola")).build();

        assertThrows(NotificationException.class, () -> client.send(sms));
    }

    @Test
    void validation_exception_is_propagated_and_no_events_are_published() {
        FakeEmailSender sender = new FakeEmailSender();

        InMemoryEventBus bus = new InMemoryEventBus();
        List<NotificationEvent> events = new ArrayList<>();
        bus.subscribe(events::add);

        NotificationClient client = NotificationClient.builder().eventBus(bus).register(sender).validator(new AlwaysInvalidValidator()).build();

        assertThrows(ValidationException.class, () -> client.send(emailNotificationNoMetadata()));

        // Validación falla antes de publicar Requested/Sent/Failed
        assertEquals(0, events.size());
        assertEquals(0, sender.calls);
    }


    @Test
    void provider_failure_is_wrapped_in_send_failed_exception_and_failed_event_is_published() {
        AlwaysFailSender sender = new AlwaysFailSender();
        NoopValidator validator = new NoopValidator();

        InMemoryEventBus bus = new InMemoryEventBus();
        List<NotificationEvent> events = new ArrayList<>();
        bus.subscribe(events::add);

        NotificationClient client = NotificationClient.builder().eventBus(bus).register(sender).validator(validator).build();

        SendFailedException ex = assertThrows(SendFailedException.class, () -> client.send(emailNotificationNoMetadata()));
        assertNotNull(ex.getCause());
        assertEquals(1, sender.calls);

        // Requested + Failed
        assertEquals(2, events.size());
        assertTrue(events.get(0) instanceof NotificationEvent.Requested);
        assertTrue(events.get(1) instanceof NotificationEvent.Failed);
    }

    @Test
    void correlation_id_is_generated_when_missing_and_used_in_events() {
        FakeEmailSender sender = new FakeEmailSender();
        NoopValidator validator = new NoopValidator();

        InMemoryEventBus bus = new InMemoryEventBus();
        List<NotificationEvent> events = new ArrayList<>();
        bus.subscribe(events::add);

        NotificationClient client = NotificationClient.builder().eventBus(bus).register(sender).validator(validator).build();

        client.send(emailNotificationNoMetadata());

        NotificationEvent.Requested req = (NotificationEvent.Requested) events.get(0);
        NotificationEvent.Sent sent = (NotificationEvent.Sent) events.get(1);

        assertNotNull(req.correlationId());
        assertFalse(req.correlationId().isBlank());
        assertEquals(req.correlationId(), sent.correlationId());
        assertTrue(req.correlationId().startsWith("corr-"));
    }

    @Test
    void retrying_sender_retries_and_succeeds() {
        FlakySender flaky = new FlakySender(2); // falla 2 veces, éxito en el 3er intento
        RetryingSender retrying = new RetryingSender(flaky, 3, Duration.ZERO);

        NotificationClient client = NotificationClient.builder().register(retrying).validator(new NoopValidator()).build();

        SendResult r = client.send(emailNotificationNoMetadata());

        assertTrue(r.success());
        assertEquals(3, flaky.calls);
        assertEquals("ok-after-retry", r.providerMessageId());
    }

    @Test
    void retrying_sender_fails_after_max_attempts() {
        FlakySender flaky = new FlakySender(5); // falla más de los intentos permitidos
        RetryingSender retrying = new RetryingSender(flaky, 3, Duration.ZERO);

        NotificationClient client = NotificationClient.builder().register(retrying).validator(new NoopValidator()).build();

        assertThrows(SendFailedException.class, () -> client.send(emailNotificationNoMetadata()));
        assertEquals(3, flaky.calls);
    }
}
