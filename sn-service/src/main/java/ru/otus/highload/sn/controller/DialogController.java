package ru.otus.highload.sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.sn.dto.DialogMessage;
import ru.otus.highload.sn.dto.DialogMessageDto;
import ru.otus.highload.sn.service.DialogService;

import java.util.List;

@RestController
@RequestMapping("/dialog")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @PostMapping("/{user_id}/send")
    public String sendMessage(@PathVariable("user_id") String userId,
                              @RequestBody DialogMessageDto dialogMessageDto,
                              @RequestParam(name = "to") String userIdTo) {
        dialogService.addMessage(userId, userIdTo, dialogMessageDto);
        return "Успешно отправлено сообщение";
    }

    @GetMapping("/{user_id}/list")
    public List<DialogMessage> getMessagesList(@PathVariable("user_id") String userId,
                                               @RequestParam(name = "to") String userIdTo) {
        return dialogService.listMessages(userId, userIdTo);
    }

}
