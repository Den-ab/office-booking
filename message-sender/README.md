# message-sender

Consumes booking events from RabbitMQ (`x.message-sender` exchange, `q.message-sender.process` queue), renders a FreeMarker email template and sends it over SMTP.

- Spring Boot 3, Spring AMQP, Spring Mail, FreeMarker
- Templates: `src/main/resources/templates/email`
- Translations (English, Russian): `src/main/resources/i18n`

## Run locally

Start RabbitMQ and Mailpit from the repository root (`docker compose up rabbitmq mailpit`), copy `.env.example` to `.env`, then run `Application` from the IDE or:

```bash
mvn spring-boot:run
```

Sent emails appear in the Mailpit inbox at http://localhost:8025.

## Tests

```bash
mvn test
```
