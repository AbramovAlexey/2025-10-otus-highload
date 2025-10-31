package ru.otus.highload.hw1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.highload.hw1.dto.LoginPost500Response;
import ru.otus.highload.hw1.exception.CustomLoginException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomLoginException.class)
    public ResponseEntity<LoginPost500Response> handleLoginError(CustomLoginException ex, HttpServletRequest request) {
        LoginPost500Response loginPost500Response = new LoginPost500Response();
        loginPost500Response.setCode(401);
        loginPost500Response.setMessage(ex.getMessage());
        loginPost500Response.setRequestId(request.getRequestId());
        return new ResponseEntity<>(loginPost500Response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
