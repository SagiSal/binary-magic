package com.binarymagic.game_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GameSession {
    private String sessionId;
    private List<Boolean> answers;
    private int currentCardIndex;
    private boolean isCompleted;
}
