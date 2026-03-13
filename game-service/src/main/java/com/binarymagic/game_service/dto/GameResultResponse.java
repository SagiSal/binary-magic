package com.binarymagic.game_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameResultResponse {
    private int result;
}
