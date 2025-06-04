package org.spacebattle.dto;

/**
 * DTO-запрос на вход в систему.
 *
 * @param username имя пользователя
 * @param password пароль пользователя
 *
 * Пример JSON:
 * {
 *   "username": "spacepilot",
 *   "password": "secret123"
 * }
 */
public record LoginRequest(String username, String password) {}
