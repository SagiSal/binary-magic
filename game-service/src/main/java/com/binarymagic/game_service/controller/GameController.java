package com.binarymagic.game_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.binarymagic.game_service.dto.AnswerRequest;
import com.binarymagic.game_service.dto.StartGameResponse;
import com.binarymagic.game_service.service.GameService;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    /**
     * Start a new game.
     * The Gateway injects X-Session-Id from the JWT — this is the player's identity.
     */
    @PostMapping("/session")
    public ResponseEntity<StartGameResponse> startGame(
            @RequestHeader("X-session-Id") String playerSessionId) {     
        
        StartGameResponse response = gameService.startGame(playerSessionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Submit an answer for the current card.
     * Returns AnswerResponse (next card) until the 6th answer,
     * then returns GameResultResponse (the number).
     * Both extend Object — Jackson serializes whichever is returned.
     */
    @PostMapping("/session/{gameSessionId}/answer")
    public ResponseEntity<Object> submitAnswer(
            @PathVariable String gameSessionId,
            @RequestHeader("X-Session-Id") String playerSessionId,
            @RequestBody AnswerRequest request) {

        Object response = gameService.submitAnswer(gameSessionId, playerSessionId, request.isAnswer());
        return ResponseEntity.ok(response);
    }


}
