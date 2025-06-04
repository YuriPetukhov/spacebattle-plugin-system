package org.spacebattle.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.spacebattle.dto.GameRecord;
import org.spacebattle.dto.GameSummary;
import org.spacebattle.service.IGameService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация сервиса управления играми {@link IGameService}.
 * <p>Хранит игры в памяти, позволяет создавать новые, проверять участие игрока и получать список игр пользователя.
 */
@Service
public class GameService implements IGameService {

    private final Map<UUID, GameRecord> games = new ConcurrentHashMap<>();

    /**
     * Создаёт новую игру, сохраняет её в локальное хранилище и уведомляет игровой сервер.
     *
     * @param name            имя игры
     * @param players         список имён игроков
     * @param shipsPerPlayer  количество кораблей на игрока
     * @return уникальный идентификатор созданной игры
     * @throws IOException если не удалось уведомить игровой сервер
     */
    @Override
    public UUID createGame(String name, List<String> players, int shipsPerPlayer) throws IOException {
        UUID gameId = UUID.randomUUID();
        GameRecord game = new GameRecord(gameId, name, players, shipsPerPlayer);
        games.put(gameId, game);
        notifyGameServer(gameId, players);
        return gameId;
    }

    /**
     * Проверяет, участвует ли указанный пользователь в заданной игре.
     *
     * @param gameId   идентификатор игры
     * @param username имя пользователя
     * @return true, если пользователь участвует в игре, иначе false
     */
    @Override
    public boolean isPlayerInGame(UUID gameId, String username) {
        GameRecord game = games.get(gameId);
        return game != null && game.players().contains(username);
    }

    /**
     * Возвращает список игр, в которых участвует указанный пользователь.
     *
     * @param username имя пользователя
     * @return список кратких описаний игр
     */
    @Override
    public List<GameSummary> getUserGames(String username) {
        List<GameSummary> result = new ArrayList<>();
        for (GameRecord game : games.values()) {
            if (game.players().contains(username)) {
                result.add(new GameSummary(game.id(), game.name()));
            }
        }
        return result;
    }

    /**
     * Отправляет POST-запрос на игровой сервер с информацией о новой игре.
     *
     * @param gameId  идентификатор игры
     * @param players список участников
     * @throws IOException если HTTP-запрос завершился с ошибкой
     */
    void notifyGameServer(UUID gameId, List<String> players) throws IOException {
        URL url = new URL("http://localhost:8082/api/game/register");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Map<String, Object> body = Map.of(
                "gameId", gameId.toString(),
                "players", players
        );

        String json = new ObjectMapper().writeValueAsString(body);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException("Игровой сервер не принял регистрацию игры: HTTP " + conn.getResponseCode());
        }
    }
}
