package ru.otus.highload.message.service;

import ru.otus.highload.message.dto.MessageDto;

public interface MessageService {

    void addMessage(MessageDto messageDto);
    void markAsRead(MessageDto messageDto);

}
