package ru.otus.highload.message.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/v1")
public class ChaosController {

    @GetMapping("/broke")
    public ResponseEntity<String> generateChaos() {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Boom! Server Error (500)");
    }
}
