package ru.otus.highload.hw1.mapper;

import org.mapstruct.Mapper;
import ru.otus.highload.hw1.dto.User;
import ru.otus.highload.hw1.dto.UserRegisterPostRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRegisterPostRequest registerPostRequest);

}
