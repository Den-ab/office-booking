package ru.otus.pw.controllers.response_dtos;

/**
 * Бронирование.
 *
 * @param id Идентификатор.
 * @param number Номер стола.
 * @param area Пространство стола.
 */
public record BookingDeskResponseDTO(String id, int number, DeskAreaResponseDTO area) {}
