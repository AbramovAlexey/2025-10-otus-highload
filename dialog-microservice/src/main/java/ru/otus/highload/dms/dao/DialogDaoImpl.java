package ru.otus.highload.dms.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.otus.highload.api.dto.DialogMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DialogDaoImpl implements DialogDao {

    private final NamedParameterJdbcOperations parameterJdbcOperations;

    @Override
    public Long addMessage(Long fromId, Long toId, String content) {
        String sql = """
                INSERT INTO messages(sender,recipient,conversation_id,content)
                            values (:sender,:recipient,:conversation_id,:content);
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("sender", fromId)
                .addValue("recipient", toId)
                .addValue("conversation_id", getConversationId(fromId, toId))
                .addValue("content", content);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        parameterJdbcOperations.update(sql, params, keyHolder);
        return Long.valueOf(Objects.requireNonNull(keyHolder.getKeys()).get("id").toString());
    }

    @Override
    public List<DialogMessage> getMessages(Long fromId, Long toId) {
        String query = """
                SELECT sender,recipient,conversation_id,content
                    FROM messages
                WHERE conversation_id = :conversation_id
                ORDER BY sent_at DESC
                """;
        return parameterJdbcOperations.query(query,
                                             Map.of("conversation_id", getConversationId(fromId, toId)),
                                             new DialogMapper());
    }

    private String getConversationId(Long fromId, Long toId) {
        return "%s_%s".formatted(Math.min(fromId, toId), Math.max(fromId, toId));
    }

    private class DialogMapper implements RowMapper<DialogMessage> {
        @Override
        public DialogMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
            var message = new DialogMessage();
            message.setFrom(rs.getString("sender"));
            message.setTo(rs.getString("recipient"));
            message.setText(rs.getString("content"));
            return message;
        }
    }

}
