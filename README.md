# Macro Tracker Common

[**← Back to Main Architecture**](https://github.com/oleh-prukhnytskyi/macro-tracker)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

---

[![License](https://img.shields.io/badge/license-Apache%202.0-blue?style=for-the-badge)](LICENSE)

**Shared library for the Macro Tracker microservices ecosystem.**

This artifact centralizes cross-cutting concerns, reusable utilities, standardized error handling, and shared domain models to enforce **DRY (Don't Repeat Yourself)** principles across the platform.

## :zap: Core Capabilities

### 1. Idempotency Mechanism
Prevents duplicate processing of critical operations (e.g., payments, form submissions) using a custom AOP aspect backed by Redis.
* **Annotation-driven**: simply add `@Idempotent` to any controller method.
* **Fingerprinting**: Generates a unique key based on User ID, Request Path, and Parameters/Body.
* **Client Control**: Supports explicit client-side idempotency keys via `X-Request-Id` header.

### 2. Standardized Error Handling (RFC 7807)
A global `RestControllerAdvice` that catches internal exceptions and maps them to a consistent `ProblemDetails` JSON structure.
* **Traceability**: Automatically injects OpenTelemetry Trace IDs into error responses.
* **Validation**: detailed breakdown of field-level validation errors.
* **Custom Exceptions**: A hierarchy of base exceptions (`BadRequestException`, `ConflictException`, `NotFoundException`) paired with specific Error Codes.

### 3. Transactional Outbox Support
Provides the JPA Entity and Repository required to implement the **Transactional Outbox Pattern**.
* **Entity**: `OutboxEvent` (stores payload, aggregate type, and processing status).
* **Repository**: `OutboxRepository` with methods to fetch unprocessed events for polling publishers.

### 4. Shared Infrastructure Config
* **Swagger/OpenAPI**: Centralized configuration for security schemes (Bearer Auth) and hiding internal headers (`X-User-Id`).
* **ShedLock**: Auto-configuration for distributed locking using JDBC.

---

## :package: Installation

Add the dependency to your `pom.xml`. This library is hosted on GitHub Packages.

```xml
<dependency>
    <groupId>com.olehprukhnytskyi</groupId>
    <artifactId>macro-tracker-common</artifactId>
    <version>${macro-tracker-common.version}</version>
</dependency>
```

---

## :gear: Configuration

Enable specific features by adding properties to your service's `application.yml`:

| Property                  | Description                                                               | Default    |
|:--------------------------|:--------------------------------------------------------------------------|:-----------|
| **Idempotency**           |                                                                           |            |
| `app.idempotency.enabled` | Enables the AOP aspect for `@Idempotent` methods.                         | `false`    |
| **Swagger**               |                                                                           |            |
| `swagger.public-url`      | The public facing URL of the service (used for Server definition in OAS). | *Required* |

---

## :hammer_and_wrench: Usage Examples

### Making an Endpoint Idempotent
Use the `@Idempotent` annotation on your controller methods. You can define a Time-To-Live (TTL) for the cache.

```java
@PostMapping
@Idempotent(ttl = 1, unit = TimeUnit.MINUTES)
public ResponseEntity<Void> createResource(@RequestBody CreateDto dto) {
    service.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
}
```

### Throwing Business Exceptions
Throw specific exceptions to return standardized HTTP error responses.

```java
if (userRepository.existsByEmail(email)) {
    throw new ConflictException(
        UserErrorCode.USER_ALREADY_EXISTS,
        "User with this email already registered"
    );
}
```

**Response Output:**
```json
{
  "title": "User already exists",
  "status": 409,
  "detail": "User with this email already registered",
  "traceId": "a1b2c3d4e5f6",
  "code": "USER_ALREADY_EXISTS"
}
```

---

## :file_folder: Domain Models

The library contains shared Enums and DTOs to ensure type safety across services:

* **Enums**: `ActivityLevel`, `Gender`, `Goal`, `IntakePeriod`, `AuthProvider`.
* **Events**: `RegistrationEvent`, `PasswordResetEvent`, `UserDeletedEvent` (used for Kafka payloads).
* **DTOs**: `PagedResponse`, `Pagination` (standardized pagination structure).

---

## :balance_scale: License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.