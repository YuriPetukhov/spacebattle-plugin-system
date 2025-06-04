package org.spacebattle.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.spacebattle.dto.GameSummary;
import org.spacebattle.dto.TokenRequest;
import org.spacebattle.dto.TokenResponse;
import org.spacebattle.service.impl.GameService;
import org.spacebattle.service.impl.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST-контроллер для управления играми и авторизацией в рамках конкретной игры.
 * <p>Позволяет создавать игры, получать список игр пользователя и выдавать токен с objectIds.</p>
 */
@RestController
@RequestMapping("/auth/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final TokenService tokenService;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * URL игрового сервера, конфигурируется через application.yml.
     */
    @Value("${game.server.url}")
    private String gameServerUrl;

    /**
     * Хранилище objectId'ов игроков по игре (gameId → username → objectIds).
     */
    private final Map<UUID, Map<String, List<String>>> gameObjectIdsMap = new ConcurrentHashMap<>();

    /**
     * Создаёт новую игру и отправляет данные на игровой сервер.
     *
     * @param body JSON-тело с параметрами: name, players, shipsPerPlayer
     * @return gameId в случае успеха или ошибка (500)
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createGame(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            List<String> players = (List<String>) body.get("players");
            int shipsPerPlayer = (int) body.get("shipsPerPlayer");

            UUID gameId = gameService.createGame(name, players, shipsPerPlayer);
            registerGameWithServer(gameId, players, shipsPerPlayer);

            return ResponseEntity.ok(Map.of("gameId", gameId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при создании игры", "details", e.getMessage()));
        }
    }

    /**
     * Возвращает список игр, в которых участвует указанный пользователь.
     *
     * @param username имя игрока
     * @return список игр
     */
    @GetMapping("/list/{username}")
    public ResponseEntity<List<GameSummary>> listUserGames(@PathVariable String username) {
        return ResponseEntity.ok(gameService.getUserGames(username));
    }

    /**
     * Выдаёт токен и список objectId'ов для указанного игрока, если он участвует в игре.
     *
     * @param request объект с gameId и username
     * @return токен и список objectIds или 403 если игрок не состоит в игре
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody TokenRequest request) {
        if (!gameService.isPlayerInGame(request.gameId(), request.username())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String token = tokenService.generateToken(request.username(), request.gameId());

        List<String> objectIds = Optional.ofNullable(gameObjectIdsMap.get(request.gameId()))
                .map(map -> map.get(request.username()))
                .orElse(List.of());

        return ResponseEntity.ok(new TokenResponse(token, objectIds));
    }

    /**
     * Вспомогательный метод, отправляющий POST-запрос на игровой сервер
     * для регистрации созданной игры и получения objectId'ов.
     */
    void registerGameWithServer(UUID gameId, List<String> players, int shipsPerPlayer) throws IOException {
        URL url = new URL(gameServerUrl + "/register");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Map<String, Object> body = Map.of(
                "gameId", gameId.toString(),
                "players", players,
                "shipsPerPlayer", shipsPerPlayer
        );

        String json = mapper.writeValueAsString(body);
        conn.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

        Map<String, List<String>> response = mapper.readValue(conn.getInputStream(), new TypeReference<>() {});
        gameObjectIdsMap.put(gameId, response);

        System.out.println("Зарегистрированы объекты: " + response);
    }
}

