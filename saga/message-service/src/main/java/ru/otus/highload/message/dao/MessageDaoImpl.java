package ru.otus.highload.message.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.otus.highload.message.dto.MessageDto;

@Component
@RequiredArgsConstructor
public class MessageDaoImpl implements MessageDao {

    private final NamedParameterJdbcOperations parameterJdbcOperations;

    @Override
    public void add(MessageDto messageDto) {
        String sql = """
                INSERT INTO messages(extId,user_to_id,content)
                            values (:extId,:user_to_id,:content);
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("extId", messageDto.getExtId())
                .addValue("user_to_id", messageDto.getUserTo())
                .addValue("content", messageDto.getContent());
        parameterJdbcOperations.update(sql, params);
    }

    @Override
    public void markAsRead(MessageDto messageDto) {
        String sql = """
                 UPDATE messages SET is_read = true
                 where extId = :extId and user_to_id = :user_to_id;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("extId", messageDto.getExtId())
                .addValue("user_to_id", messageDto.getUserTo());
        parameterJdbcOperations.update(sql, params);
    }

}
