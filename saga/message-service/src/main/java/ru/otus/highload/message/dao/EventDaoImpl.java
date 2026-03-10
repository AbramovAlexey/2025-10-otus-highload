package ru.otus.highload.message.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.highload.message.dto.EventDto;
import ru.otus.highload.message.dto.EventType;

import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class EventDaoImpl implements EventDao {

    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcOperations parameterJdbcOperations;

    @Override
    public void addCreateEvent(UUID userId) {
        registerEvent(userId, EventType.CREATED);
    }

    @Override
    public void addReadEvent(UUID userId) {
        registerEvent(userId, EventType.READED);
    }

    @SneakyThrows
    private void registerEvent(UUID userId, EventType eventType) {
        var dto = EventDto.builder()
                          .eventType(eventType)
                          .userId(userId)
                          .build();
        String sql = """
                INSERT INTO outbox_events(message_key, payload)
                            values (:message_key,  CAST(:payload AS jsonb));
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("message_key", userId)
                .addValue("payload", objectMapper.writeValueAsString(dto));
        parameterJdbcOperations.update(sql, params);
    }

}
