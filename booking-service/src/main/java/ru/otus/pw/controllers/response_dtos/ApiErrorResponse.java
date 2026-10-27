package ru.otus.pw.controllers.response_dtos;

import java.time.Instant;
import java.util.Map;

/**
 * Унифицированный ответ API с ошибкой.
 *
 * @param message Общая ошибка.
 * @param fieldErrors Ошибки по полям формы.
 * @param timestamp Время формирования ошибки.
 */
public record ApiErrorResponse(
    String message,
    Map<String, String> fieldErrors,
    Instant timestamp
) {
    public ApiErrorResponse(String message) {

        this(message, Map.of(), Instant.now());
    }

    public ApiErrorResponse(String message, Map<String, String> fieldErrors) {

        this(message, fieldErrors == null ? Map.of() : fieldErrors, Instant.now());
    }
}
