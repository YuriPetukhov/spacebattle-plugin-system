package org.spacebattle.dto;

import java.util.List;

/**
 * DTO-ответ на запрос токена.
 *
 * Используется для передачи JWT токена и списка ID игровых объектов, доступных игроку.
 *
 * @param token JWT токен авторизации
 * @param objectIds список идентификаторов объектов, привязанных к игроку
 *
 * Пример JSON:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR...",
 *   "objectIds": ["object-1", "object-2"]
 * }
 */
public record TokenResponse(String token, List<String> objectIds) {}
