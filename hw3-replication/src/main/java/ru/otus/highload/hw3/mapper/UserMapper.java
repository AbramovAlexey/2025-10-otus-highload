package ru.otus.highload.hw3.mapper;

import org.mapstruct.Mapper;
import ru.otus.highload.hw3.dto.User;
import ru.otus.highload.hw3.dto.UserRegisterPostRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRegisterPostRequest registerPostRequest);

}
