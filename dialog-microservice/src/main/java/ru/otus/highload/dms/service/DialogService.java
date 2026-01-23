package ru.otus.highload.dms.service;

import ru.otus.highload.api.dto.DialogMessage;
import ru.otus.highload.dms.dto.DialogMessageRestDto;

import java.util.List;

public interface DialogService {

    void addMessage(String userId, String userIdTo, DialogMessageRestDto dialogMessageRestDto);
    List<DialogMessage> listMessages(String userId, String userIdTo);

}
