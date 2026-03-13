package com.binarymagic.game_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AnswerResponse {
    private int cardIndex;
    private List<Integer> numbers;
}