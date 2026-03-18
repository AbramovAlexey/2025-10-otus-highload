package ru.otus.highload.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.highload.message.dao.EventDao;
import ru.otus.highload.message.dao.MessageDao;
import ru.otus.highload.message.dto.MessageDto;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;
    private final EventDao eventDao;

    @Override
    @Transactional
    public void addMessage(MessageDto messageDto) {
        messageDao.add(messageDto);
        eventDao.addCreateEvent(messageDto.getUserTo());
    }

    @Override
    @Transactional
    public void markAsRead(MessageDto messageDto) {
        messageDao.markAsRead(messageDto);
        eventDao.addReadEvent(messageDto.getUserTo());
    }

}
