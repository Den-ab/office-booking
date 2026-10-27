package ru.otus.pw.controllers.response_dtos;

/**
 * Стол.
 *
 * @param id Идентификатор.
 * @param number Номер стола.
 * @param area Пространство стола.
 */
public record DeskResponseDTO(String id, int number, DeskAreaResponseDTO area) {}
