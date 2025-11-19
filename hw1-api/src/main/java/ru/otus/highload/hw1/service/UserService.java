package ru.otus.highload.hw1.service;

import ru.otus.highload.hw1.dto.LoginPostRequest;
import ru.otus.highload.hw1.dto.User;
import ru.otus.highload.hw1.dto.UserRegisterPostRequest;

public interface UserService {

    User findById(Long id);
    Long register(UserRegisterPostRequest registerPostRequest);
    String login(LoginPostRequest loginPostRequest);

}
