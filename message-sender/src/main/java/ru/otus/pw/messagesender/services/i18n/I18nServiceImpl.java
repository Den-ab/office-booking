package ru.otus.pw.messagesender.services.i18n;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.pw.messagesender.config.email.SenderConfig;

import java.util.Locale;

/**
 * Сервис интернационализации.
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "message-sender")
public class I18nServiceImpl implements I18nService {
    //region Fields

    /**
     * Сервис получения текста по указанному ключу.
     */
    private final MessageSource messageSource;

    /**
     * Общие настройки для формирования писем.
     */
    private final SenderConfig senderConfig;

    //endregion
    //region Public

    @Override
    public String getMessage(String key, Object... args) {
        return getMessage(senderConfig.getLocale(), key, args);
    }

    @Override
    public String getMessage(Locale locale, String key, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

    @Override
    public String getMessage(String lang, String key, Object... args) {
        var locale = Locale.forLanguageTag(lang);
        return messageSource.getMessage(key, args, locale);
    }

    //endregion
}
