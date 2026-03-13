package com.binarymagic.game_service.config;

import com.binarymagic.game_service.model.GameSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @SuppressWarnings("deprecation")
    @Bean
    public RedisTemplate<String, GameSession> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, GameSession> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Keys stored as plain strings — readable in Redis CLI
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Values stored as JSON — also readable in Redis CLI
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())               // handles Instant
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<GameSession> valueSerializer =
            new Jackson2JsonRedisSerializer<>(mapper, GameSession.class);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}

