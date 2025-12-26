package ru.otus.highload.sn.dao;

import ru.otus.highload.sn.dto.User;

import java.util.List;

public interface UserDao {

    List<Long> getSubscribers(Long authorId);

}
