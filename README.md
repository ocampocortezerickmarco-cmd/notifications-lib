# Notifications Library (Framework-agnostic)

Librería Java (sin frameworks) para enviar notificaciones por múltiples canales (**Email**, **SMS**, **Push**) con proveedores intercambiables.

> **Objetivo:** ofrecer una API unificada para enviar notificaciones sin acoplar el código cliente a un proveedor específico (SendGrid, Twilio, FCM, etc.).  
> El envío real está **simulado** (sin HTTP real) para enfocarse en arquitectura, extensibilidad y diseño.

---

## Características

-  API unificada para múltiples canales
-  3 canales obligatorios: **Email**, **SMS**, **Push**
-  Configuración **100% Java code** (sin Spring, sin YAML/properties)
-  Proveedores intercambiables por canal (Strategy)
-  Validación por canal
-  Manejo de errores con excepciones claras
-  Envío asíncrono (`CompletableFuture`)
-  Reintentos (`RetryingSender`, patrón Decorator)
-  Eventos de ciclo de vida (`Requested`, `Sent`, `Failed`) con Observer/EventBus
-  Tests unitarios con stubs/fakes (sin integraciones reales)

---

## Requisitos

- **Java 21+**
- **Maven 3.9+** (o compatible)

---

## Instalación

### Opción 1: Usar este repositorio directamente (recomendado para la prueba)
Clona el repositorio y ejecuta:

```bash
mvn clean test
mvn clean package
