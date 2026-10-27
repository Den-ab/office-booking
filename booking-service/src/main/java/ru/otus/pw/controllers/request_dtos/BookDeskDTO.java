package ru.otus.pw.controllers.request_dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * Бронирование.
 *
 * @param userId Идентификатор пользователя.
 * @param deskId Идентификатор стола.
 */
public record BookDeskDTO(
    @NotBlank(message = "ID пользователя обязателен")
    String userId,
    @NotBlank(message = "ID стола обязателен")
    String deskId
) { }
