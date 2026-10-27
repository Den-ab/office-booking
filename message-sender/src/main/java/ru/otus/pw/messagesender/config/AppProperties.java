package ru.otus.pw.messagesender.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.pw.messagesender.config.email.EmailConfig;

import java.util.Locale;

/**
 * Конфигурация приложения.
 */
@ConfigurationProperties(prefix = "application")
@Getter
public class AppProperties implements EmailConfig {
    //region Fields

    /**
     * Путь до темплейтов для email.
     */
    private final String emailTemplatesPath;

    /**
     * Российский адрес отправителя электронной почты.
     */
    private final String emailSenderAddress;

    /**
     * Путь до логотипа.
     */
    private final String logoPath;

    /**
     * Локаль приложения.
     */
    private final Locale locale;

    //endregion
    //region Ctor

    /**
     * Конструктор пропертей.
     *
     * @param emailTemplatesPath Путь до темплейтов для email.
     * @param emailSenderAddress Российский адрес отправителя электронной почты.
     * @param logoPath Путь до логотипа.
     * @param locale Локаль приложения
     */
    public AppProperties(String emailTemplatesPath, String emailSenderAddress, String logoPath, String locale) {

        this.emailTemplatesPath = emailTemplatesPath;
        this.emailSenderAddress = emailSenderAddress;
        this.logoPath = logoPath;
        this.locale = Locale.forLanguageTag(locale);
    }

    //endregion
}
