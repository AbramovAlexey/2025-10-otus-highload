package ru.otus.highload.counter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import ru.otus.highload.counter.dto.Payload;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CounterServiceImpl implements CounterService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> safeDecrementScript;
    
    @Override
    public void increase(Payload payload,  Long idempotencyId) {
        String counterKey = getCounterKey(payload);
        if (isNew(idempotencyId)) {
            var cnt = redisTemplate.opsForValue().increment(counterKey);
            log.info("Counter increase - {}, for user {}", cnt, payload.getUserId());
        }
    }
    
    @Override
    public void decrease(Payload payload,  Long idempotencyId) {
        String counterKey = getCounterKey(payload);
        if (isNew(idempotencyId)) {
            var cnt = redisTemplate.execute(safeDecrementScript, Collections.singletonList(counterKey));
            log.info("Counter decrease - {}, for user {}", cnt, payload.getUserId());
        }
    }

    @Override
    public int getByUserId(UUID userId) {
        String counterKey = getCounterKey(userId);
        var val = redisTemplate.opsForValue().get(counterKey);
        return val == null ? 0 : Integer.parseInt(val);
    }

    private static String getCounterKey(Payload payload) {
        return getCounterKey(payload.getUserId());
    }

    private static String getCounterKey(UUID userId) {
        return "unread:" + userId;
    }
    
    private boolean isNew(Long idempotencyId) {
        String lockKey = "msg_processed:" + idempotencyId;
        var isNew =  Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "true", Duration.ofHours(24)));
        if (!isNew) {
            log.warn("Message with idempotencyId {} will be ignored - alreay processed", idempotencyId);
        }
        return isNew;
    }
    
}
