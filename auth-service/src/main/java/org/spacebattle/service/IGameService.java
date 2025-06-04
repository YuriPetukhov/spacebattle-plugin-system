package org.spacebattle.service;

import org.spacebattle.dto.GameSummary;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IGameService {
    UUID createGame(String name, List<String> players, int shipsPerPlayer) throws IOException;

    boolean isPlayerInGame(UUID gameId, String username);

    List<GameSummary> getUserGames(String username);
}
