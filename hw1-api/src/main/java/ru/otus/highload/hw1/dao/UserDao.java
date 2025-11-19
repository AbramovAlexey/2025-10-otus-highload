package ru.otus.highload.hw1.dao;

import ru.otus.highload.hw1.dto.User;

public interface UserDao {

    User findById(Long id);
    Long insert(User newUser, String encodedPassword);
    String getEncodedPasswordByUserId(Long id);

}
