package ru.otus.highload.dms.dao;


import ru.otus.highload.api.dto.DialogMessage;

import java.util.List;

public interface DialogDao {

    Long addMessage(Long fromId, Long toId, String content);
    List<DialogMessage> getMessages(Long fromId, Long toId);

}
