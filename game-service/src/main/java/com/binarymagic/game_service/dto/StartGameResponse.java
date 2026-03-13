package com.binarymagic.game_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StartGameResponse {
    private String sessionId;
    private int cardIndex;
    private List<Integer> numbers;   // the numbers on this card
}
