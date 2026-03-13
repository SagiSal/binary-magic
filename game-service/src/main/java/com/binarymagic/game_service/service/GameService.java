package com.binarymagic.game_service.service;

import com.binarymagic.game_service.dto.AnswerResponse;
import com.binarymagic.game_service.dto.GameResultResponse;
import com.binarymagic.game_service.dto.StartGameResponse;
import com.binarymagic.game_service.exception.GameNotFoundException;
import com.binarymagic.game_service.exception.GameAlreadyCompleteException;
import com.binarymagic.game_service.model.GameSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final BinaryCardService binaryCardService;
    private final GameStateService gameStateService;
    private final GameEventPublisher eventPublisher;

    // ── Start a new game ───────────────────────────────────────────────────────

    public StartGameResponse startGame(String playerSessionId) {
        String gameSessionId = UUID.randomUUID().toString();

        GameSession session = GameSession.builder()
            .sessionId(gameSessionId)
            .playerSessionId(playerSessionId)
            .answers(new ArrayList<>())
            .currentCardIndex(0)
            .status(GameSession.GameStatus.IN_PROGRESS)
            .startedAt(Instant.now())
            .build();

        gameStateService.save(session);

        log.info("Started game {} for player {}", gameSessionId, playerSessionId);

        return StartGameResponse.builder()
            .sessionId(gameSessionId)
            .cardIndex(0)
            .numbers(binaryCardService.getCards().get(0))
            .build();
    }



    public Object submitAnswer(String gameSessionId, String playerSessionId, boolean answer) {
        // 1. Load from Redis — 404 if not found or expired
        GameSession session = gameStateService.get(gameSessionId)
            .orElseThrow(() -> new GameNotFoundException(gameSessionId));

        // 2. Guard: can't answer a completed game
        if (session.getStatus() == GameSession.GameStatus.COMPLETED) {
            throw new GameAlreadyCompleteException(gameSessionId);
        }

        // 3. Record the answer
        session.getAnswers().add(answer);
        session.setCurrentCardIndex(session.getCurrentCardIndex() + 1);

        // 4. Are we done?
        if (session.getCurrentCardIndex() >= binaryCardService.getCards().size()) {
            return completeGame(session);
        }

        // 5. Not done — save updated state and return next card
        gameStateService.save(session);

        return AnswerResponse.builder()
            .cardIndex(session.getCurrentCardIndex())
            .numbers(binaryCardService.getCards().get(session.getCurrentCardIndex()))
            .build();
    }

    // ── Internal: handle game completion ──────────────────────────────────────

    private GameResultResponse completeGame(GameSession session) {
        int result = computeResult(session.getAnswers());
        long durationMs = Duration.between(session.getStartedAt(), Instant.now()).toMillis();

        session.setStatus(GameSession.GameStatus.COMPLETED);
        gameStateService.save(session);

        // Fire event — failure here is logged but never thrown
        eventPublisher.publishGameCompleted(
            session.getSessionId(),
            session.getPlayerSessionId(),
            result,
            durationMs
        );

        log.info("Game {} completed — result: {}, duration: {}ms",
            session.getSessionId(), result, durationMs);

        return GameResultResponse.builder()
            .result(result)
            .build();
    }


    public int computeResult(List<Boolean> answers) {
        int result = 0;

        for (int bit = 0; bit < answers.size(); bit++) {
            if (answers.get(bit)) {
                result |= (1 << bit);
            }
        }

        return result;
}
}
