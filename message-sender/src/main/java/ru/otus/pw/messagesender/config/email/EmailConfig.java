package ru.otus.pw.messagesender.config.email;

/**
 * Интерфейс конфигурации отправки сообщений на электронную почту.
 */
public interface EmailConfig extends SenderConfig {
    //region Public

    /**
     * Возвращает путь до темплейтов формирующих сообщение на электронную почту.
     *
     * @return Путь до темплейтов.
     */
    String getEmailTemplatesPath();

    /**
     * Возвращает российский адрес того от кого отправляется сообщение на электронную почту.
     *
     * @return Адрес отправителя электронной почты.
     */
    String getEmailSenderAddress();

    //endregion
}
