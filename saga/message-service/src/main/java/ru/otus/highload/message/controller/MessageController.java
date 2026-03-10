package ru.otus.highload.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.message.dto.MessageDto;
import ru.otus.highload.message.service.MessageService;

@RestController
@RequestMapping("/v1/messages/")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("create")
    public void create(@RequestBody MessageDto messageDto) {
        messageService.addMessage(messageDto);
    }

    @PutMapping("mark-readed")
    public void markAsRead(@RequestBody MessageDto messageDto) {
        messageService.markAsRead(messageDto);
    }



}
