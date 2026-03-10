package ru.otus.highload.message.dao;


import ru.otus.highload.message.dto.MessageDto;

public interface MessageDao {

    void add(MessageDto dto);
    void markAsRead(MessageDto dto);

}
