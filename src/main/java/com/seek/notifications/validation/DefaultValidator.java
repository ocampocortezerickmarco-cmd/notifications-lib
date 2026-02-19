package com.seek.notifications.validation;

import com.seek.notifications.exceptions.ValidationException;
import com.seek.notifications.models.*;
import com.seek.notifications.spi.NotificationValidator;

import java.util.regex.Pattern;

/**
 * Validador por defecto.
 * <p>
 * Aplica reglas mínimas por canal (email válido, teléfono E.164, token no vacío, etc.).
 * Se ejecuta antes de seleccionar/enviar al proveedor.
 * <p>
 * Nota: si se desea OCP más estricto, puede reemplazarse por validadores por canal
 * registrados en un mapa (Channel -> ChannelValidator) para evitar modificar este archivo
 * al agregar nuevos canales.
 */

public final class DefaultValidator implements NotificationValidator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    @Override
    public void validate(Notification n) {
        switch (n.channel()) {
            case EMAIL -> validateEmail(n);
            case SMS -> validateSms(n);
            case PUSH -> validatePush(n);
        }
    }

    private void validateEmail(Notification n) {
        if (!(n.recipient() instanceof EmailRecipient r))
            throw new ValidationException("EMAIL requires EmailRecipient");
        if (!EMAIL.matcher(r.email()).matches()) throw new ValidationException("Invalid email: " + r.email());
        if (!(n.message() instanceof EmailMessage m)) throw new ValidationException("EMAIL requires EmailMessage");
        if (m.subject().isBlank()) throw new ValidationException("Email subject is blank");
        if (m.body().isBlank()) throw new ValidationException("Email body is blank");
    }

    private void validateSms(Notification n) {
        if (!(n.recipient() instanceof PhoneRecipient r)) throw new ValidationException("SMS requires PhoneRecipient");
        if (!E164.matcher(r.e164Phone()).matches())
            throw new ValidationException("Invalid phone (E.164): " + r.e164Phone());
        if (!(n.message() instanceof SmsMessage m)) throw new ValidationException("SMS requires SmsMessage");
        if (m.text().isBlank()) throw new ValidationException("SMS text is blank");
    }

    private void validatePush(Notification n) {
        if (!(n.recipient() instanceof DeviceRecipient r))
            throw new ValidationException("PUSH requires DeviceRecipient");
        if (r.deviceToken().isBlank()) throw new ValidationException("Device token is blank");
        if (!(n.message() instanceof PushMessage m)) throw new ValidationException("PUSH requires PushMessage");
        if (m.title().isBlank()) throw new ValidationException("Push title is blank");
        if (m.body().isBlank()) throw new ValidationException("Push body is blank");
    }
}