package ru.otus.highload.sn.service;

import ru.otus.highload.sn.dto.DialogMessage;
import ru.otus.highload.sn.dto.DialogMessageDto;

import java.util.List;

public interface DialogService {

    void addMessage(String userId, String userIdTo, DialogMessageDto dialogMessageDto);
    List<DialogMessage> listMessages(String userId, String userIdTo);

}
