package com.binarymagic.game_service.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String sessionId) {
        super("Game session not found: " + sessionId + " (may have expired)");
    }
}
