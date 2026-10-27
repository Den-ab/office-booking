package ru.otus.pw.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.pw.controllers.response_dtos.ApiErrorResponse;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Глобальный обработчик исключений.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //region Public

    /**
     * Ошибки валидации @Valid для @RequestBody.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с ошибками полей.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex
    ) {

        final Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse("Ошибка валидации", errors));
    }

    /**
     * Ошибки биндинга параметров.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с ошибками полей.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBindException(BindException ex) {

        final Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse("Ошибка валидации", errors));
    }

    /**
     * Ошибки неверных аргументов, например UUID.fromString(...).
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с общей ошибкой.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse(
                ex.getMessage() != null && !ex.getMessage().isBlank()
                    ? ex.getMessage()
                    : "Переданы некорректные данные"
            ));
    }

    /**
     * Ошибки аутентификации.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с общей ошибкой.
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(Exception ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponse("Неверный логин или пароль"));
    }

    /**
     * Ошибки доступа.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с общей ошибкой.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiErrorResponse("Недостаточно прав для выполнения операции"));
    }

    /**
     * Ошибки отсутствия сущности.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с общей ошибкой.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponse(
                ex.getMessage() != null && !ex.getMessage().isBlank()
                    ? ex.getMessage()
                    : "Сущность не найдена"
            ));
    }

    /**
     * Ошибки бизнес-логики конфликта.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с общей ошибкой.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse(
                ex.getMessage() != null && !ex.getMessage().isBlank()
                    ? ex.getMessage()
                    : "Конфликт состояния"
            ));
    }

    /**
     * Неожиданные ошибки.
     *
     * @param ex Исключение.
     *
     * @return Тело ответа с общей ошибкой.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse("Внутренняя ошибка сервера"));
    }

    //endregion
}
