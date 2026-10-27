package ru.otus.pw.config.rabbit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Проперти обмена сообщениями
 *
 * @param mail настройки для процесса - почта (приемка АС)
 * @param deadLetter настройки для dead letter
 */
@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(
    @NestedConfigurationProperty MailProperties mail,
    @NestedConfigurationProperty DeadLetterProperties deadLetter,
    @NestedConfigurationProperty MessageSenderProperties messageSender
) {
    //region Nested classes

    /**
     * Проперти сообщения для сервиса почты.
     *
     * @param exchange Обменник.
     * @param queue Очередь.
     * @param routingKey Ключ привязки.
     */
    public record MailProperties(ExchangeProperties exchange, MailQueues queue, MailRoutingKeys routingKey) {}

    /**
     * Проперти мертвой очереди.
     *
     * @param exchange Обменник.
     * @param returnExchange Очередь для возврата в нее сообщений.
     * @param queue Очередь.
     * @param routingKey Ключ привязки.
     */
    public record DeadLetterProperties(
        ExchangeProperties exchange,
        ExchangeProperties returnExchange,
        QueueProperties queue,
        String routingKey
    ) {}

    /**
     * Проперти сообщения для распознавания.
     *
     * @param exchange Обменник.
     * @param routingKey Ключ привязки.
     * @param queue Очередь.
     */
    public record MessageSenderProperties(ExchangeProperties exchange, QueueProperties queue, String routingKey) {}

    /**
     * Проперти для очередей почты.
     *
     * @param inboundRequest Входящий запрос на приемку.
     * @param response Исходящий ответ.
     */
    public record MailQueues(QueueProperties inboundRequest, QueueProperties response) {}

    /**
     * Проперти для ключей почты.
     *
     * @param inboundRequest Входящий запрос на приемку.
     * @param response Исходящий ответ.
     */
    public record MailRoutingKeys(String inboundRequest, String response) {}

    /**
     * Проперти очереди.
     *
     * @param concurrency Количество потоков для обработки очереди.
     * @param name Имя очереди.
     * @param messageTTL Время жизни сообщения.
     */
    public record QueueProperties(String concurrency, String name, Integer messageTTL) {}

    /**
     * Проперти exchange (роутера).
     *
     * @param name Имя.
     */
    public record ExchangeProperties(String name) {}

    //endregion

}
