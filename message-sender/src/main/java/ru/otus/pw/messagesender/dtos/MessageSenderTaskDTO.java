package ru.otus.pw.messagesender.dtos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Данные для создания таски на отправку уведомления.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
@Builder(toBuilder = true)
@Getter
public class MessageSenderTaskDTO {
    //region Fields

    /**
     * Данные которые необходимо отправить в уведомлении.
     */
    private final Map<String, Object> messageFields;

    /**
     * Адрес отправки уведомления.
     */
    private final String messageAddress;

    /**
     * Тип темплейта уведомления.
     */
    private final String messageTemplateType;

    /**
     * Кто отправляет письмо
     */
    private final String emailSender;

    //endregion
}
