package com.binarymagic.game_service.exception;

public class GameAlreadyCompleteException extends RuntimeException {
    public GameAlreadyCompleteException(String sessionId) {
        super("Game session already completed: " + sessionId);
    } 
}
