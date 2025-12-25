package ru.otus.highload.sn.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostDaoImpl implements PostDao {

    private final NamedParameterJdbcOperations parameterJdbcOperations;

    @Override
    public List<Post> getPosts(Long userId) {
        log.debug("Query for DB for materialized posts for user {}", userId);
        String query = """
                SELECT id, content, author_id
                    FROM feed_materialize
                WHERE subscriber_id = :userId
                ORDER BY id desc; 
                """;
        return parameterJdbcOperations.query(query, Map.of("userId", userId), new PostMapper());
    }

    @Override
    public Long add(PostCreatePostRequest postCreatePostRequest, Long authorId) {
        String sql = """
                INSERT INTO posts(content, author_user_id)
                            values (:content, :authorId);
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", postCreatePostRequest.getText())
                .addValue("authorId", authorId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        parameterJdbcOperations.update(sql, params, keyHolder);
        return Long.valueOf(Objects.requireNonNull(keyHolder.getKeys()).get("id").toString());
    }

    @Override
    public Long addToFeed(String content, Long postId, Long subId, Long authorId) {
        String sql = """
                INSERT INTO feed_materialize(post_id,content,subscriber_id,author_id)
                            values (:post_id,:content,:subscriber_id,:author_id);
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("author_id", authorId)
                .addValue("post_id", postId)
                .addValue("content", content)
                .addValue("subscriber_id", subId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        parameterJdbcOperations.update(sql, params, keyHolder);
        return Long.valueOf(Objects.requireNonNull(keyHolder.getKeys()).get("id").toString());

    }

    private static class PostMapper implements RowMapper<Post> {

        @Override
        public Post mapRow(ResultSet resultSet, int i) throws SQLException {
            var post = new Post();
            post.setId(resultSet.getString("id"));
            post.setText(resultSet.getString("content"));
            post.setAuthorUserId(resultSet.getString("author_id"));
            return post;
        }

    }

}
