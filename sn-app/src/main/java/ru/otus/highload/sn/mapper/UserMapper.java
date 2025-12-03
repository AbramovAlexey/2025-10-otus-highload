package ru.otus.highload.sn.mapper;

import org.mapstruct.Mapper;
import ru.otus.highload.sn.dto.User;
import ru.otus.highload.sn.dto.UserRegisterPostRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRegisterPostRequest registerPostRequest);

}
