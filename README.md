# Notifications Library (Framework-agnostic)

Librería Java (sin frameworks) para enviar notificaciones por múltiples canales (**Email**, **SMS**, **Push**) con proveedores intercambiables.  
El envío real está **simulado** (no se realizan conexiones HTTP) para enfocarse en arquitectura, extensibilidad y buenas prácticas.

## Requirements
- Java 21+
- Maven

## Build & Test
```bash
mvn clean test
mvn clean package
