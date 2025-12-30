package ru.otus.highload.sn.dao;

import ru.otus.highload.sn.dto.DialogMessage;

import java.util.List;

public interface DialogDao {

    Long addMessage(Long fromId, Long toId, String content);
    List<DialogMessage> getMessages(Long fromId, Long toId);

    default String getConversationId(Long fromId, Long toId) {
        return "%s_%s".formatted(Math.min(fromId, toId), Math.max(fromId, toId));
    }

}
