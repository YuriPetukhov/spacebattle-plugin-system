package org.spacebattle.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private final String secret = "very-secret-key-that-is-long-enough-for-hmac-256!";
    private final long validityMs = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(secret, validityMs);
    }

    @Test
    void generateAndValidateToken_withGameId_shouldReturnTrue() {
        UUID gameId = UUID.randomUUID();
        String token = tokenService.generateToken("testUser", gameId);

        assertTrue(tokenService.validateToken(token));
        assertTrue(tokenService.isValid(token));
        assertEquals("testUser", tokenService.getUsernameFromToken(token));
        assertEquals(gameId, tokenService.getGameIdFromToken(token));
    }

    @Test
    void generateAndValidateToken_withoutGameId_shouldReturnTrue() {
        String token = tokenService.generateToken("anotherUser");

        assertTrue(tokenService.validateToken(token));
        assertEquals("anotherUser", tokenService.getUsernameFromToken(token));
    }

    @Test
    void validateToken_shouldReturnFalse_forInvalidToken() {
        String fakeToken = "not.a.real.token";

        assertFalse(tokenService.validateToken(fakeToken));
        assertFalse(tokenService.isValid(fakeToken));
    }

    @Test
    void getGameIdFromToken_shouldThrow_forTokenWithoutGameId() {
        String token = tokenService.generateToken("user");

        assertThrows(NullPointerException.class, () ->
                tokenService.getGameIdFromToken(token));
    }
}
