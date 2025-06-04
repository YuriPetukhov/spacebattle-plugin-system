package org.spacebattle.dto;

import java.util.UUID;

/**
 * DTO-запрос для получения токена.
 *
 * Используется при подключении к игре: пользователь указывает свой логин и UUID игры.
 *
 * @param username имя пользователя
 * @param gameId уникальный идентификатор игры
 *
 * Пример JSON:
 * {
 *   "username": "player1",
 *   "gameId": "8b4f6d3e-24ab-4f2a-8a4c-9a304e9854e6"
 * }
 */
public record TokenRequest(String username, UUID gameId) {}
