package ru.otus.highload.sn.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    @Override
    @Cacheable
    public Boolean isCelebrityById(Long id) {
        String query = """
                SELECT is_seleb
                    FROM users
                WHERE id=:id LIMIT 1
                """;
        return parameterJdbcOperations.queryForObject(query, Map.of("id", id), Boolean.class);
    }

}
