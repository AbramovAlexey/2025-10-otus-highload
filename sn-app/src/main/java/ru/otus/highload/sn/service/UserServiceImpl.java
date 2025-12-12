package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.highload.sn.exception.CustomLoginException;
import ru.otus.highload.sn.dao.UserDao;
import ru.otus.highload.sn.dto.LoginPostRequest;
import ru.otus.highload.sn.dto.User;
import ru.otus.highload.sn.dto.UserRegisterPostRequest;
import ru.otus.highload.sn.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public Long register(UserRegisterPostRequest registerPostRequest) {
        String encodedPassword = passwordEncoder.encode(registerPostRequest.getPassword());
        return userDao.insert(userMapper.toUser(registerPostRequest), encodedPassword);
    }

    @Override
    public String login(LoginPostRequest loginPostRequest) {
        try {
            String storedEncodedPassword = userDao.getEncodedPasswordByUserId(Long.valueOf(loginPostRequest.getId()));
            if (!passwordEncoder.matches(loginPostRequest.getPassword(), storedEncodedPassword)) {
               throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new CustomLoginException("Incorrect id/password");
        }
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByFI(String firstName, String lastName) {
        return userDao.findByFI(firstName, lastName);
    }

}
