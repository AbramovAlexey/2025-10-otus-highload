package ru.otus.highload.dms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.api.dto.DialogMessage;
import ru.otus.highload.dms.dto.DialogMessageRestDto;
import ru.otus.highload.dms.service.DialogService;

import java.util.List;

@RestController
@RequestMapping("/dialog/v2")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @PostMapping("/{user_id}/send")
    public String sendMessage(@PathVariable("user_id") String userId,
                              @RequestBody DialogMessageRestDto dialogMessageRestDto,
                              @RequestParam(name = "to") String userIdTo) {
        dialogService.addMessage(userId, userIdTo, dialogMessageRestDto);
        return "Успешно отправлено сообщение";
    }

    @GetMapping("/{user_id}/list")
    public List<DialogMessage> getMessagesList(@PathVariable("user_id") String userId,
                                               @RequestParam(name = "to") String userIdTo) {
        return dialogService.listMessages(userId, userIdTo);
    }

}
