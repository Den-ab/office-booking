package ru.otus.pw.controllers.request_dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Стол.
 *
 * @param id Идентификатор пользователя.
 * @param areaId Идентификатор пространства.
 * @param number Номер стола.
 */
public record DeskDTO(
    String id,
    @NotBlank(message = "ID пространства обязателен")
    String areaId,
    @Positive(message = "Номер стола должен быть положительным")
    int number
) { }
