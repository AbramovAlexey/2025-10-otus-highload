package ru.otus.highload.counter.service;

import ru.otus.highload.counter.dto.Payload;

import java.util.UUID;

public interface CounterService {

    void increase(Payload payload, Long idempotencyId);
    void decrease(Payload payload, Long idempotencyId);

    int getByUserId(UUID userId);

}
