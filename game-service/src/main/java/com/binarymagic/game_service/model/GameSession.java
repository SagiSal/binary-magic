package com.binarymagic.game_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSession implements Serializable {
    
    private String sessionId;
    private String playerSessionId;    // from X-Session-Id header (JWT claim)

    private List<Boolean> answers;     // grows from 0 to 6 as player answers

    @Builder.Default
    private Instant startedAt = Instant.now();

    @Builder.Default
    private int currentCardIndex = 0;
    
    @Builder.Default
    private GameStatus status = GameStatus.IN_PROGRESS;
    
    public enum GameStatus {
        IN_PROGRESS,
        COMPLETED,
        ABANDONED
    }
}
