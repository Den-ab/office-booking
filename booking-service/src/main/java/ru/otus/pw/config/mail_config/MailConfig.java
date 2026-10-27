package ru.otus.pw.config.mail_config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Проперти для mail sender.
 *
 * @param emailSender От чьего имени отправляем письмо
 */
@ConfigurationProperties(prefix = "app.mail")
public record MailConfig(String emailSender) { }
