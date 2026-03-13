package com.binarymagic.game_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${game.rabbit.exchange:game.events}")
    private String exchange;

    @Value("${game.rabbit.routing-key.completed:game.session.completed}")
    private String completedKey;

    @Value("${game.rabbit.routing-key.abandoned:game.session.abandoned}")
    private String abandonedKey;

    public void publishGameCompleted(String sessionId, String playerSessionId,
                                     int result, long durationMs) {
        publish(completedKey, Map.of(
            "eventId",        sessionId + ":completed",
            "sessionId",      sessionId,
            "playerSessionId", playerSessionId,
            "result",         result,
            "durationMs",     durationMs,
            "occurredAt",     Instant.now().toString()
        ));
    }

    public void publishGameAbandoned(String sessionId, String playerSessionId) {
        publish(abandonedKey, Map.of(
            "eventId",         sessionId + ":abandoned",
            "sessionId",       sessionId,
            "playerSessionId", playerSessionId,
            "occurredAt",      Instant.now().toString()
        ));
    }

    private void publish(String routingKey, Object payload) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
            log.debug("Published event to {}/{}", exchange, routingKey);
        } catch (Exception e) {
            // Analytics is optional — never let it break gameplay
            log.error("Failed to publish event to {}/{}: {}", exchange, routingKey, e.getMessage());
        }
    }
}

