package ru.otus.highload.sn.dao;

import java.util.List;

public interface UserDao {

    List<Long> getSubscribers(Long authorId);
    Boolean isCelebrityById(Long id);
}
