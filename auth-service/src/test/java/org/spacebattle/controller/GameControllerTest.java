package org.spacebattle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.dto.GameSummary;
import org.spacebattle.dto.TokenRequest;
import org.spacebattle.service.impl.GameService;
import org.spacebattle.service.impl.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @MockBean
    private TokenService tokenService;

    private final UUID gameId = UUID.randomUUID();
    private final String username = "player1";
    private final List<String> players = List.of("player1", "player2");
    private final int shipsPerPlayer = 3;
    private final Map<UUID, Map<String, List<String>>> gameObjectIdsMap = new ConcurrentHashMap<>();

    @BeforeEach
    void setup() throws IOException {
        when(gameService.createGame(anyString(), anyList(), anyInt())).thenReturn(gameId);
        when(gameService.getUserGames(username)).thenReturn(List.of(new GameSummary(gameId, "test")));
        when(gameService.isPlayerInGame(eq(gameId), eq(username))).thenReturn(true);
        when(tokenService.generateToken(eq(username), eq(gameId))).thenReturn("mockedToken");
    }

    @Test
    void testCreateGameSuccess() throws Exception {
        mockMvc.perform(post("/auth/game/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "Test Game",
                              "players": ["player1", "player2"],
                              "shipsPerPlayer": 3
                            }
                        """))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testListUserGamesSuccess() throws Exception {
        mockMvc.perform(get("/auth/game/list/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(gameId.toString()))
                .andExpect(jsonPath("$[0].name").value("test"));
    }

    @Test
    void testGetTokenSuccess() throws Exception {
        Map<String, List<String>> gameMap = new HashMap<>();
        gameMap.put(username, List.of("obj1", "obj2"));
        gameObjectIdsMap.put(gameId, gameMap);

        TokenRequest request = new TokenRequest(username, gameId);

        mockMvc.perform(post("/auth/game/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockedToken"));
    }

    @Test
    void testGetTokenForbidden() throws Exception {
        when(gameService.isPlayerInGame(eq(gameId), eq("unknown"))).thenReturn(false);

        TokenRequest request = new TokenRequest("unknown", gameId);

        mockMvc.perform(post("/auth/game/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateGameFails_returnsServerError() throws Exception {
        when(gameService.createGame(any(), any(), anyInt())).thenThrow(new RuntimeException("mock"));

        mockMvc.perform(post("/auth/game/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Test Game",
                          "players": ["player1", "player2"],
                          "shipsPerPlayer": 3
                        }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Ошибка при создании игры"))
                .andExpect(jsonPath("$.details").value("mock"));
    }

    @Test
    void testGetTokenReturnsEmptyObjectIdsIfMissing() throws Exception {
        when(gameService.isPlayerInGame(eq(gameId), eq(username))).thenReturn(true);
        when(tokenService.generateToken(eq(username), eq(gameId))).thenReturn("mockedToken");

        TokenRequest request = new TokenRequest(username, gameId);

        mockMvc.perform(post("/auth/game/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockedToken"))
                .andExpect(jsonPath("$.objectIds").isEmpty());
    }
}
