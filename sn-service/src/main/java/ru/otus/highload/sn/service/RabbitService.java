package ru.otus.highload.sn.service;

public interface RabbitService {

    void sendEventUser(String text, Long postId, Long authorId);

}
