package com.binarymagic.game_service.service;

import com.binarymagic.game_service.model.GameSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameStateService {

    private final RedisTemplate<String, GameSession> redisTemplate;

    @Value("${game.session.ttl-minutes:30}")
    private int ttlMinutes;

    private static final String KEY_PREFIX = "game:session:";

    public void save(GameSession session) {
        String key = key(session.getSessionId());
        redisTemplate.opsForValue().set(key, session, Duration.ofMinutes(ttlMinutes));
        log.debug("Saved game session {} (TTL {}m)", session.getSessionId(), ttlMinutes);
    }

    public Optional<GameSession> get(String sessionId) {
        GameSession session = redisTemplate.opsForValue().get(key(sessionId));
        return Optional.ofNullable(session);
    }

    public void delete(String sessionId) {
        redisTemplate.delete(key(sessionId));
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}