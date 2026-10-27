package ru.otus.pw.messagesender.services;

import ru.otus.pw.messagesender.dtos.MessageSenderTaskDTO;

/**
 * Интерфейс сервиса отправки уведомлений.
 */
public interface MessageSenderService {
    //region Public

    /**
     * Формирует и отправляет сообщение по указанной задачи на уведомление.
     *
     * @param task Задача на уведомление.
     */
    void sendMessage(MessageSenderTaskDTO task);

    //endregion
}
