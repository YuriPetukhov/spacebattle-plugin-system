package org.spacebattle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Клиент для обращения к удалённому сервису верификации токенов.
 *
 * <p>Использует {@link HttpClient} для отправки HTTP-запроса с токеном
 * в заголовке Authorization на указанный URL. Ожидается, что сервис вернёт
 * HTTP 200 в случае успешной верификации.</p>
 */
public class TokenVerifierClient {

    /**
     * URL удалённого сервиса, проверяющего токены.
     */
    private final String verifierUrl;

    /**
     * HTTP-клиент, используемый для отправки запросов.
     */
    private final HttpClient httpClient;

    /**
     * Создаёт клиента верификации токенов с явным {@link HttpClient}.
     *
     * @param verifierUrl URL сервиса верификации токенов
     * @param httpClient  HTTP-клиент для отправки запросов
     */
    public TokenVerifierClient(String verifierUrl, HttpClient httpClient) {
        this.verifierUrl = verifierUrl;
        this.httpClient = httpClient;
    }

    /**
     * Создаёт клиента верификации токенов с дефолтным {@link HttpClient}.
     *
     * @param verifierUrl URL сервиса верификации токенов
     */
    public TokenVerifierClient(String verifierUrl) {
        this(verifierUrl, HttpClient.newHttpClient());
    }

    /**
     * Проверяет валидность переданного токена с помощью удалённого сервиса.
     *
     * <p>Отправляет GET-запрос на {@code verifierUrl} с заголовком {@code Authorization: Bearer <token>}.
     * Возвращает {@code true}, если сервис вернул HTTP 200, и {@code false} в остальных случаях
     * или при возникновении исключений.</p>
     *
     * @param token токен, подлежащий верификации
     * @return {@code true}, если токен действителен, иначе {@code false}
     */
    public boolean verify(String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(verifierUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Ошибка при верификации токена: " + e.getMessage());
            return false;
        }
    }
}
