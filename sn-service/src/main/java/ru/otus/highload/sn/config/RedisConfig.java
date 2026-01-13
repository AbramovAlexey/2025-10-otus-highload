package ru.otus.highload.sn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.List;

@Configuration
public class RedisConfig {

    public static final String KEY_PREFIX = "dialog";
    public static final String INDEX_KEY_PREFIX = "conversation_idx";

    @Bean
    public RedisAtomicLong idCounter(RedisConnectionFactory connectionFactory) {
        return new RedisAtomicLong(KEY_PREFIX + "counter", connectionFactory);
    }

    @Bean
    public RedisScript<String> insertScript() {
        return RedisScript.of(new ClassPathResource("lua/insert.lua"));
    }

    @Bean
    public RedisScript<List> selectScript() {
        return RedisScript.of(new ClassPathResource("lua/select.lua"), List.class);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

}
