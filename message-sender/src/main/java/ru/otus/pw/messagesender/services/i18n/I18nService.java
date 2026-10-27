package ru.otus.pw.messagesender.services.i18n;

import java.util.Locale;

/**
 * Интерфейс сервиса интернационализации.
 */
public interface I18nService {
    //region Public

    /**
     * Возвращает сообщение на языке заданном в системе (по умолчанию), ключу сообщения и параметрам
     * подстановки в сообщение.
     *
     * @param key Ключ сообщения.
     * @param args Параметры подстановки в сообщение.
     *
     * @return Сообщение.
     */
    String getMessage(String key, Object... args);

    /**
     * Возвращает сообщение согласно локали, ключу сообщения и параметрам
     * подстановки в сообщение.
     *
     * @param locale Локаль
     * @param key Ключ сообщения.
     * @param args Параметры подстановки в сообщение.
     *
     * @return Сообщение.
     */
    String getMessage(Locale locale, String key, Object... args);

    /**
     * Возвращает сообщение согласно переданному языку, ключу сообщения и параметрам
     * подстановки в сообщение.
     *
     * @param lang Язык
     * @param key Ключ сообщения.
     * @param args Параметры подстановки в сообщение.
     *
     * @return Сообщение.
     */
    String getMessage(String lang, String key, Object... args);

    //endregion
}

