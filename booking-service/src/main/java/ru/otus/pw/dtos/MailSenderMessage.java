package ru.otus.pw.dtos;

import lombok.Builder;

import java.util.Map;

/**
 * Данные для отправки писем.
 *
 * @param messageType Тип отправляемого сообщения.
 * @param messageAddress Адрес на которое отправляется письмо.
 * @param messageTemplateType Наименование шаблона письма.
 * @param emailSender Кто будет отправлять письмо.
 * @param messageFields Динамические значения шаблона.
 */
@Builder(toBuilder = true)
public record MailSenderMessage(
    String messageType,
    String messageAddress,
    String messageTemplateType,
    String emailSender,
    Map<String, Object> messageFields
) {

}
