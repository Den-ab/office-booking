package ru.otus.pw.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Проперти JWT.
 *
 * @param secret Секрет.
 * @param expiration Срок истечения.
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, long expiration) {}
