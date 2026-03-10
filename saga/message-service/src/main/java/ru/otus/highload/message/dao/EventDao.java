package ru.otus.highload.message.dao;

import java.util.UUID;

public interface EventDao {

    void addCreateEvent(UUID userId);
    void addReadEvent(UUID userId);

}
