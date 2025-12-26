package ru.otus.highload.sn.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.highload.sn.dto.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final NamedParameterJdbcOperations parameterJdbcOperations;

    @Override
    @Cacheable
    public List<Long> getSubscribers(Long authorId) {
        String query = """
                SELECT subscriber_id
                    FROM subscribers 
                    WHERE author_id = :authorId;
                """;
        return parameterJdbcOperations.queryForList(query, Map.of("authorId", authorId), Long.class);
    }

}
