package ru.otus.pw.controllers.response_dtos;

/**
 * Пользователь.
 *
 * @param id Идентификатор.
 * @param email Почта.
 * @param firstName Имя.
 * @param lastName Фамилия.
 * @param role Роль.
 */
public record UserResponseDTO(
    String id,
    String email,
    String firstName,
    String lastName,
    String role
) {}
