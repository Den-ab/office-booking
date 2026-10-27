package ru.otus.pw.controllers.request_dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Данные для логина пользователя.
 *
 * @param email Почта.
 * @param password Пароль.
 */
public record LoginDTO(
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email некорректный")
    String email,
    @NotBlank(message = "Пароль обязателен")
    String password
) {}
