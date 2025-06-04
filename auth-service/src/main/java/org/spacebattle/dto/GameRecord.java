package org.spacebattle.dto;

import java.util.List;
import java.util.UUID;

/**
 * DTO, представляющее сохранённую (или активную) игру.
 *
 * @param id              уникальный идентификатор игры
 * @param name            имя игры
 * @param players         список имён участников
 * @param shipsPerPlayer  количество кораблей на одного игрока
 *
 * Пример:
 * {
 *   "id": "c5fa93ba-9051-4c3e-9c66-c1be36cf5baf",
 *   "name": "GalaxyBattle",
 *   "players": ["alice", "bob"],
 *   "shipsPerPlayer": 2
 * }
 */
public record GameRecord(UUID id, String name, List<String> players, int shipsPerPlayer) {}
