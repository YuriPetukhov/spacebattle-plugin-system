package org.spacebattle.dto;

import java.util.UUID;

/**
 * DTO-ответ, содержащий идентификатор созданной или найденной игры.
 *
 * Используется для передачи UUID игры от сервера клиенту.
 *
 * Пример:
 * {
 *   "gameId": "b7a8f23c-9876-4c4b-ae35-c8fdb83a94a9"
 * }
 */
public record GameIdResponse(UUID gameId) {}
