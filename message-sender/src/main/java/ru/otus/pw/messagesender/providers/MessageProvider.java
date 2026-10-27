package ru.otus.pw.messagesender.providers;

/**
 * Функция для получения сервиса интернационализации внутри шаблонов.
 */
@FunctionalInterface
public interface MessageProvider {
    //region Public

    String getMessage(String key, Object... args);

    //endregion
}
