package ru.otus.pw.messagesender.config.email;

import java.util.Locale;

/**
 * Интерфейс общих настроек для формирования писем.
 */
public interface SenderConfig {
    //region Public

    /**
     * Возвращает путь до файла логотипа.
     *
     * @return Путь до логитипа.
     */
    String getLogoPath();

    /**
     * Возвращает локаль.
     *
     * @return Локаль.
     */
    Locale getLocale();

    //endregion
}
