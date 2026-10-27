# booking-service

REST API of Office Booking: authentication, areas, desks and bookings.

- Spring Boot 3, Spring Web MVC, Spring Data JPA, Spring Security with JWT
- PostgreSQL with Flyway migrations (`src/main/resources/db/migration`)
- Publishes a booking event to RabbitMQ; the email itself is sent by [message-sender](../message-sender)

## Run locally

Start the infrastructure from the repository root (`docker compose up postgres rabbitmq mailpit`), copy `.env.example` to `.env`, then run `Application` from the IDE or:

```bash
mvn spring-boot:run
```

The API listens on http://localhost:9010.

## Tests

```bash
mvn test
```
