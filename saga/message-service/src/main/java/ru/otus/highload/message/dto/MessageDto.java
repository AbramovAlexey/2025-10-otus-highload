package ru.otus.highload.message.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageDto {

    UUID extId;
    UUID userTo;
    String content;

}
