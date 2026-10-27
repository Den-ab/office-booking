package ru.otus.pw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.otus.pw.config.mail_config.MailConfig;
import ru.otus.pw.config.rabbit.MessagingProperties;
import ru.otus.pw.dtos.MailSenderMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с почтой.
 */
@Service
@RequiredArgsConstructor
public class MailSenderService {
    //region Constant

    /**
     * Дефолтное значение типа сообщения
     */
    private static final String DEFAULT_MESSAGE_TYPE = "EMAIL";

    /**
     * Дефолтное значение шаблона сообщения о приглашении пользователя в папку.
     */
    private static final String OFFICE_BOOKING_DESK_TEMPLATE = "OFFICE_BOOKING_DESK_TEMPLATE";

    /**
     * Тема сообщения бронирования.
     */
    private static final String BOOKING_MESSAGE_SUBJECT = "Бронирование стола в офисе";

    //endregion
    //region Fields

    /**
     * Конфиг для message sender
     */
    private final MailConfig mailConfig;

    /**
     * <p>API для работы с очередью.</p>
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * <p>Messaging проперти.</p>
     */
    private final MessagingProperties messagingProperties;

    //endregion
    //region Public

    /**
     * Отправляет сообщение о бронировании стола.
     *
     * @param email Почта для отправки.
     * @param areaName Наименование пространства.
     * @param tableNumber Номер стола.
     * @param bookingDate Дата бронирования.
     */
    public void sendBookingMessageToMail(String email, String areaName, int tableNumber, String bookingDate) {

        Map<String, Object> messageFields = new HashMap<>();
        messageFields.put("tableNumber", tableNumber);
        messageFields.put("bookingDate", bookingDate);
        messageFields.put("spaceName", areaName);
        messageFields.put("subject", MailSenderService.BOOKING_MESSAGE_SUBJECT);
        MailSenderMessage messageSenderDTO = MailSenderMessage.builder()
            .messageTemplateType(MailSenderService.OFFICE_BOOKING_DESK_TEMPLATE)
            .messageType(MailSenderService.DEFAULT_MESSAGE_TYPE)
            .messageAddress(email)
            .emailSender(this.mailConfig.emailSender())
            .messageFields(messageFields)
            .build();

        this.sendMail(messageSenderDTO);
    }

    //endregion
    //region Private

    /**
     * Отправляет письмо в очередь почтового сервиса.
     *
     * @param message Сообщение.
     */
    private void sendMail(MailSenderMessage message) {

        this.rabbitTemplate.convertAndSend(
            this.messagingProperties.messageSender().exchange().name(),
            this.messagingProperties.messageSender().routingKey(),
            message
        );
    }

    //endregion
}
