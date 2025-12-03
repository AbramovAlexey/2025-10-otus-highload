package ru.otus.highload.sn.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.highload.sn.dto.LoginPost500Response;
import ru.otus.highload.sn.exception.CustomLoginException;

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
