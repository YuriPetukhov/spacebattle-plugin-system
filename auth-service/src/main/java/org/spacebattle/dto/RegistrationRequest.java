package org.spacebattle.dto;

/**
 * DTO-запрос на регистрацию пользователя.
 *
 * @param username имя нового пользователя
 * @param password пароль нового пользователя
 *
 * Пример JSON:
 * {
 *   "username": "newuser",
 *   "password": "strongpass123"
 * }
 */
public record RegistrationRequest(String username, String password) {}
