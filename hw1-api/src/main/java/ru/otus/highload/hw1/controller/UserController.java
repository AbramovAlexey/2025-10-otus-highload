package ru.otus.highload.hw1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.hw1.dto.*;
import ru.otus.highload.hw1.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginPost200Response login(@RequestBody LoginPostRequest loginPostRequest) {
        var response =  new LoginPost200Response();
        String token = userService.login(loginPostRequest);
        response.setToken(token);
        return response;
    }

    @PostMapping("/user/register")
    public UserRegisterPost200Response register(@RequestBody UserRegisterPostRequest registerPostRequest) {
        UserRegisterPost200Response response = new UserRegisterPost200Response();
        response.setUserId(String.valueOf(userService.register(registerPostRequest)));
        return response;
    }

    @GetMapping("user/get/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("user/search")
    public List<User> searchByFI(@RequestParam String first_name, @RequestParam String last_name) {
        return userService.findByFI(first_name, last_name);
    }

}
