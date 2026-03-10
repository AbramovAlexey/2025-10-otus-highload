package ru.otus.highload.counter.kafka;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.otus.highload.counter.dto.EventDto;
import ru.otus.highload.counter.dto.EventType;
import ru.otus.highload.counter.dto.Payload;
import ru.otus.highload.counter.service.CounterService;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventListener {

    private final ObjectMapper objectMapper;
    private final CounterService counterService;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(
            topics = "my_server.public.outbox_events",
            groupId = "counter-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOutboxEvent(String message, Acknowledgment ack) {
        try {
            log.info("Received event from Debezium: {}", message);

            EventDto eventDto = objectMapper.readValue(message, EventDto.class);
            Payload payload = objectMapper.readValue(eventDto.getPayload(), Payload.class);

            switch (payload.getEventType()) {
                case CREATED -> counterService.increase(payload, eventDto.getId());
                case READED -> counterService.decrease(payload, eventDto.getId());
                default -> log.warn("Unknown message type {}", payload.getEventType());
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process counter event, sending to retry: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @DltHandler
    public void handleDlt(String message) {
        log.error("EVENT LOST: Message reached DLT after all retries: {}", message);
        //для исправления вручную, либо исправится в рамках работы задания по расписанию по
        //"выравниваю" счетчиков - в рамках ДЗ не реализовывалосьл
    }

}
