package ru.otus.pw.controllers.response_dtos;

/**
 * Бронирование.
 *
 * @param id Идентификатор.
 * @param date Дата бронирования.
 * @param user Пользователь.
 * @param desk Стол.
 */
public record BookingResponseDTO(String id, String date, BookingUserResponseDTO user, BookingDeskResponseDTO desk) {}
