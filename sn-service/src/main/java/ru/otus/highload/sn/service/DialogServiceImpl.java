package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.highload.sn.dao.DialogDao;
import ru.otus.highload.sn.dto.DialogMessage;
import ru.otus.highload.sn.dto.DialogMessageDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DialogServiceImpl implements DialogService {

    private final DialogDao dialogDao;


    @Override
    public void addMessage(String userId, String userIdTo, DialogMessageDto dialogMessageDto) {
        dialogDao.addMessage(Long.valueOf(userId), Long.valueOf(userIdTo), dialogMessageDto.getText());
    }

    @Override
    public List<DialogMessage> listMessages(String userId, String userIdTo) {
        return dialogDao.getMessages(Long.valueOf(userId), Long.valueOf(userIdTo));
    }

}
