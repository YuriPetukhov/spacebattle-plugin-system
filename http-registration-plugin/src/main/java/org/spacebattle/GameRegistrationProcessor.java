package org.spacebattle;


import org.spacebattle.dsl.FileDslSource;
import org.spacebattle.ioc.IoC;
import org.spacebattle.dsl.DslLoader;
import org.spacebattle.dsl.DslSource;

import java.io.File;
import java.util.*;

/**
 * Обрабатывает регистрацию игроков в игре.
 * Загружает объекты игроков из YAML-файлов (через DSL) и возвращает токен с результатом.
 */
public class GameRegistrationProcessor {

    private final IoC ioc;

    public GameRegistrationProcessor(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Регистрирует игроков, загружая их объекты из DSL YAML-файлов.
     * @param request карта вида { "players": ["user1", "user2"] }
     * @return статус и сгенерированный токен
     * @throws Exception если произошла ошибка загрузки
     */
    public Map<String, Object> register(Map<String, Object> request) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> users = (List<String>) request.get("players");

        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("Поле 'players' не должно быть пустым");
        }


        @SuppressWarnings("unchecked")
        DslLoader<Void> loader = (DslLoader<Void>) ioc.resolve("dsl-object-loader");

        for (String user : users) {
            File file = new File("objects/" + user + ".yaml");
            if (!file.exists()) {
                System.err.println("Не найден YAML-файл: " + file.getPath());
                continue;
            }

            DslSource source = new FileDslSource(file);
            loader.load(source);
        }


        String token = UUID.randomUUID().toString();
        return Map.of("status", "ok", "token", token, "registered", users);
    }
}
