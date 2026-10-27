package ru.otus.pw.controllers.response_dtos;

/**
 * Ответ успешного логина.
 *
 * @param userId Идентификатор текущего пользователя.
 * @param token Токен.
 * @param role Роль.
 */
public record AuthResponse(String userId, String token, String role) {}
