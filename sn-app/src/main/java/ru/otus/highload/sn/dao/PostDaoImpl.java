package ru.otus.highload.sn.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostDaoImpl implements PostDao {

    private final NamedParameterJdbcOperations parameterJdbcOperations;

    @Override
    public List<Post> getPosts(Integer offset, Integer limit) {
        String query = """
                SELECT id, "content", author_user_id
                    FROM posts
                ORDER BY id desc LIMIT :limit OFFSET :offset;
                """;

        return parameterJdbcOperations.query(query, Map.of("offset", offset, "limit", limit), new PostMapper());
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
