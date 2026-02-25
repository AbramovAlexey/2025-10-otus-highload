package ru.otus.highload.dms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.highload.api.dto.DialogMessage;
import ru.otus.highload.dms.dao.DialogDao;
import ru.otus.highload.dms.dto.DialogMessageRestDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DialogServiceImpl implements DialogService {

    private final DialogDao dialogDao;


    @Override
    public void addMessage(String userId, String userIdTo, DialogMessageRestDto dialogMessageRestDto) {
        dialogDao.addMessage(Long.valueOf(userId), Long.valueOf(userIdTo), dialogMessageRestDto.getText());
    }

    @Override
    public List<DialogMessage> listMessages(String userId, String userIdTo) {
        return dialogDao.getMessages(Long.valueOf(userId), Long.valueOf(userIdTo));
    }

}
