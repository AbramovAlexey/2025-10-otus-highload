package ru.otus.highload.sn.service;

import ru.otus.highload.sn.dto.LoginPostRequest;
import ru.otus.highload.sn.dto.User;
import ru.otus.highload.sn.dto.UserRegisterPostRequest;

import java.util.List;

public interface UserService {

    User findById(Long id);
    Long register(UserRegisterPostRequest registerPostRequest);
    String login(LoginPostRequest loginPostRequest);

    List<User> findByFI(String firstName, String lastName);
}
