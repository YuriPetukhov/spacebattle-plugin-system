package org.spacebattle.dto;

import java.util.List;

/**
 * DTO-запрос для создания новой игры.
 *
 * Содержит список имён пользователей, которые должны участвовать в игре.
 *
 * Пример JSON:
 * {
 *   "playerUsernames": ["alice", "bob", "charlie"]
 * }
 */
public record GameCreationRequest(List<String> playerUsernames) {}
