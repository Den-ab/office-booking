package ru.otus.pw.messagesender.exceptions;

/**
 * Ошибка, которая возникает при отправке уведомления на электронную почту.
 */
public class EmailSenderException extends RuntimeException {
    //region Ctor

    /**
     * Конструктор исключения.
     *
     * @param message Сообщение.
     * @param ex Исключение.
     */
    public EmailSenderException(String message, Throwable ex) {

        super(message, ex);
    }

    //endregion
}
