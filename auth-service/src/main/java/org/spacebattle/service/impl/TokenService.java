package org.spacebattle.service.impl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.spacebattle.service.ITokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Реализация {@link ITokenService} для генерации и валидации JWT-токенов.
 * <p>Использует алгоритм HMAC-SHA256 и заданный секретный ключ.</p>
 */
@Service
public class TokenService implements ITokenService {

    /**
     * Секретный ключ для подписи токенов.
     */
    private final SecretKey secretKey;

    /**
     * Время жизни токена в миллисекундах.
     */
    private final long validityInMilliseconds;

    /**
     * Конструктор TokenService.
     *
     * @param secretKey              строка-секрет, используется для подписи
     * @param validityInMilliseconds срок действия токена в мс
     */
    public TokenService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.validity}") long validityInMilliseconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /**
     * Генерирует JWT-токен, включающий имя пользователя и ID игры.
     *
     * @param username имя пользователя
     * @param gameId   идентификатор игры
     * @return JWT-токен
     */
    @Override
    public String generateToken(String username, UUID gameId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .claim("gameId", gameId.toString())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Генерирует JWT-токен только с именем пользователя (без gameId).
     *
     * @param username имя пользователя
     * @return JWT-токен
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет корректность JWT-токена: подпись, срок действия и т.д.
     *
     * @param token строка токена
     * @return true, если токен валиден
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token строка токена
     * @return subject (имя пользователя)
     */
    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Извлекает идентификатор игры из JWT-токена.
     *
     * @param token строка токена
     * @return UUID игры
     */
    @Override
    public UUID getGameIdFromToken(String token) {
        String gameIdStr = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("gameId", String.class);
        return UUID.fromString(gameIdStr);
    }

    /**
     * Синоним для {@link #validateToken(String)}.
     *
     * @param token JWT-токен
     * @return true, если токен действителен
     */
    @Override
    public boolean isValid(String token) {
        return validateToken(token);
    }
}
