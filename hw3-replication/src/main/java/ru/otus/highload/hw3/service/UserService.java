package ru.otus.highload.hw3.service;

import ru.otus.highload.hw3.dto.LoginPostRequest;
import ru.otus.highload.hw3.dto.User;
import ru.otus.highload.hw3.dto.UserRegisterPostRequest;

import java.util.List;

public interface UserService {

    User findById(Long id);
    Long register(UserRegisterPostRequest registerPostRequest);
    String login(LoginPostRequest loginPostRequest);

    List<User> findByFI(String firstName, String lastName);
}
