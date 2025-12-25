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
    public List<Post> getPosts(Integer offset, Integer limit) {
        log.debug("Query for DB for posts limit {} offset {}", limit, offset);
        String query = """
                SELECT id, "content", author_user_id
                    FROM posts
                ORDER BY id desc LIMIT :limit OFFSET :offset;
                """;
        return parameterJdbcOperations.query(query, Map.of("offset", offset, "limit", limit), new PostMapper());
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

    private static class PostMapper implements RowMapper<Post> {

        @Override
        public Post mapRow(ResultSet resultSet, int i) throws SQLException {
            var post = new Post();
            post.setId(resultSet.getString("id"));
            post.setText(resultSet.getString("content"));
            post.setAuthorUserId(resultSet.getString("author_user_id"));
            return post;
        }

    }

}
