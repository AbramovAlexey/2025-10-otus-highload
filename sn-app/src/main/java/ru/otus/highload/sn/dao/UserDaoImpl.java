package ru.otus.highload.sn.dao;

import lombok.RequiredArgsConstructor;
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
    public User findById(Long id) {
        String query = """
                SELECT id, first_name, second_name, birth_date, biography, city, password
                    FROM users
                WHERE id=:id
                """;
        return parameterJdbcOperations.queryForObject(query, Map.of("id", id), new UserMapper());
    }

    @Override
    public Long insert(User newUser, String passwordEncoded) {
        String sql = """
                INSERT INTO users(first_name,second_name,birth_date,biography,city,password)
                            values (:first_name,:second_name,:birth_date,:biography,:city,:password);
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("first_name", newUser.getFirstName())
                .addValue("second_name", newUser.getSecondName())
                .addValue("birth_date", newUser.getBirthdate())
                .addValue("biography", newUser.getBiography())
                .addValue("city", newUser.getCity())
                .addValue("password", passwordEncoded);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        parameterJdbcOperations.update(sql, params, keyHolder);
        return Long.valueOf(Objects.requireNonNull(keyHolder.getKeys()).get("id").toString());
    }

    @Override
    public String getEncodedPasswordByUserId(Long id) {
        String query = """
                SELECT password
                    FROM users
                WHERE id=:id LIMIT 1
                """;
        return parameterJdbcOperations.queryForObject(query, Map.of("id", id), String.class);
    }

    @Override
    public List<User> findByFI(String firstName, String lastName) {
        String query = """
                SELECT id, first_name, second_name, birth_date, biography, city, password
                    FROM users
                WHERE first_name like :firstName and second_name like :secondName
                ORDER BY id
                """;
        return parameterJdbcOperations.query(query, Map.of("firstName", firstName + "%", "secondName", lastName + "%"), new UserMapper());
    }

    private static class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            var user = new User();
            user.setId(resultSet.getString("id"));
            user.setFirstName(resultSet.getString("first_name"));
            user.setSecondName(resultSet.getString("second_name"));
            user.setBirthdate(resultSet.getDate("birth_date").toLocalDate());
            user.setBiography(resultSet.getString("biography"));
            user.setCity(resultSet.getString("city"));
            return user;
        }

    }

}
