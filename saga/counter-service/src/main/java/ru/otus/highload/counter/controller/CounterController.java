package ru.otus.highload.counter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.highload.counter.service.CounterService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/counter/")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @GetMapping("user")
    public Integer create(@RequestParam UUID userId) {
        return counterService.getByUserId(userId);
    }

}
