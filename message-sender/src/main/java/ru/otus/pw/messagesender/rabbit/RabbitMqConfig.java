package ru.otus.pw.messagesender.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки RabbitMQ компонентов.
 */
@Configuration
public class RabbitMqConfig {
    //region Constants

    /**
     * Наименование очереди задач.
     */
    public static final String PROCESS_QUEUE_NAME = "q.message-sender.process";

    /**
     * Наименование обменника задач.
     */
    public static final String EXCHANGE_NAME = "x.message-sender";

    /**
     * Ключ маршрутизации для основной очереди задач.
     */
    public static final String PROCESS_ROUTING_KEY = "process";

    //endregion
    //region Public

    /**
     * Создает основную очередь задач.
     *
     * @return очередь задач.
     */
    @Bean
    public Queue taskQueue() {

        return QueueBuilder.durable(RabbitMqConfig.PROCESS_QUEUE_NAME).build();
    }

    /**
     * Создает обменник для задач.
     *
     * @return обменник задач.
     */
    @Bean
    public Exchange exchange() {

        return ExchangeBuilder.directExchange(RabbitMqConfig.EXCHANGE_NAME).build();
    }

    /**
     * Создает биндинг для основной очереди задач.
     *
     * @param taskQueue основная очередь задач.
     * @param exchange  обменник задач.
     * @return биндинг для основной очереди задач.
     */
    @Bean
    public Binding taskBinding(Queue taskQueue, Exchange exchange) {

        return BindingBuilder.bind(taskQueue).to(exchange).with(RabbitMqConfig.PROCESS_ROUTING_KEY).noargs();
    }

    /**
     * Создает и настраивает шаблон RabbitTemplate для взаимодействия с RabbitMQ.
     *
     * @param connectionFactory фабрика подключений к RabbitMQ.
     * @return настроенный шаблон RabbitTemplate.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setExchange(RabbitMqConfig.EXCHANGE_NAME);

        return template;
    }

    /**
     * Создает конвертер сообщений в формате JSON для RabbitMQ.
     *
     * @return конвертер сообщений в JSON.
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    //endregion
}
