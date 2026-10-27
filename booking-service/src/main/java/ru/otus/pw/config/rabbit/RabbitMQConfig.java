package ru.otus.pw.config.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Конфигурация брокера сообщений RabbitMQ.</p>
 */
@Slf4j
@AllArgsConstructor
@Configuration
public class RabbitMQConfig {
    //region Fields

    /**
     * <p>Маппер.</p>
     */
    private final ObjectMapper objectMapper;

    //endregion
    //region Public

    /**
     * <p>Заменяет стандартный конвертер на JSON конвертер.</p>
     *
     * @return Бин {@link RabbitTemplate}.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());

        return rabbitTemplate;
    }

    /**
     * <p>Возвращает JSON конвертер.</p>
     *
     * @return Бин {@link Jackson2JsonMessageConverter}.
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {

        return new Jackson2JsonMessageConverter(this.objectMapper);
    }

    /**
     * <p>Возвращает api для работы с очередью.</p>
     *
     * @param cachingConnectionFactory Фабрика подключений к брокеру.
     *
     * @return Бин {@link RabbitTemplate}.
     */
    @Bean
    public RabbitTemplate jsonRabbitTemplate(
        Jackson2JsonMessageConverter converter,
        CachingConnectionFactory cachingConnectionFactory
    ) {

        final RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);

        return template;
    }

    //endregion
}
