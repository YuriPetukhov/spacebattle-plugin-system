package org.spacebattle.service;

import java.util.UUID;

public interface ITokenService {
    String generateToken(String username, UUID gameId);

    String generateToken(String username);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);

    UUID getGameIdFromToken(String token);

    boolean isValid(String token);
}
