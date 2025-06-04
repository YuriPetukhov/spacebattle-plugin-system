package org.spacebattle.dto;

import java.util.UUID;

/**
 * Краткое описание игры для отображения в списке.
 *
 * @param id   уникальный идентификатор игры
 * @param name имя игры
 *
 * Пример:
 * {
 *   "id": "e321d726-5de0-4a70-b8a3-ef5a64d6b200",
 *   "name": "Galactic Clash"
 * }
 */
public record GameSummary(UUID id, String name) {}
