package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static ru.otus.highload.sn.RabbitConfig.EXCHANGE_NAME;
import static ru.otus.highload.sn.RabbitConfig.ROUTING_KEY_USER;


@Service
@RequiredArgsConstructor
public class RabbitServiceImpl implements RabbitService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendEventUser(String text, Long postId, Long authorId) {
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME,
                ROUTING_KEY_USER.replace("*", String.valueOf(authorId)),
                text
        );
    }

}
