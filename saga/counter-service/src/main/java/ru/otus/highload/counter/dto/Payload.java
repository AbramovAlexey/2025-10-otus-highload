package ru.otus.highload.counter.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class Payload {

    private UUID userId;
    private EventType eventType;

}