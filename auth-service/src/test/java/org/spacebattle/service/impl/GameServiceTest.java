package org.spacebattle.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.dto.GameSummary;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = spy(new GameService());
    }

    @Test
    void createGame_shouldAddGameToMapAndReturnId() throws Exception {
        doNothing().when(gameService).notifyGameServer(any(), any());

        List<String> players = List.of("Alice", "Bob");
        UUID gameId = gameService.createGame("TestGame", players, 2);

        assertNotNull(gameId);
        assertTrue(gameService.isPlayerInGame(gameId, "Alice"));
        assertTrue(gameService.isPlayerInGame(gameId, "Bob"));
    }

    @Test
    void isPlayerInGame_shouldReturnFalseForUnknownGame() {
        assertFalse(gameService.isPlayerInGame(UUID.randomUUID(), "Alice"));
    }

    @Test
    void getUserGames_shouldReturnOnlyGamesWhereUserIsParticipant() throws Exception {
        doNothing().when(gameService).notifyGameServer(any(), any());

        UUID game1 = gameService.createGame("G1", List.of("Alice"), 1);
        UUID game2 = gameService.createGame("G2", List.of("Bob"), 1);
        UUID game3 = gameService.createGame("G3", List.of("Alice", "Bob"), 1);

        List<GameSummary> games = gameService.getUserGames("Alice");

        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(g -> g.id().equals(game1)));
        assertTrue(games.stream().anyMatch(g -> g.id().equals(game3)));
    }

    @Test
    void createGame_shouldThrowIfNotifyFails() throws IOException {
        GameService service = new GameService() {
            @Override
            public void notifyGameServer(UUID gameId, List<String> players) throws IOException {
                throw new IOException("mocked failure");
            }
        };

        IOException ex = assertThrows(IOException.class, () ->
                service.createGame("demo", List.of("u"), 1)
        );

        assertEquals("mocked failure", ex.getMessage());
    }

}
