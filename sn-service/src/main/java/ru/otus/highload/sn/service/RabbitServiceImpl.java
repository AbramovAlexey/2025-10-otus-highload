package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static ru.otus.highload.sn.config.RabbitConfig.*;


@Service
@RequiredArgsConstructor
public class RabbitServiceImpl implements RabbitService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendEventUser(String text, Long postId, Long authorId) {
        sendInternal(text, postId, authorId, ROUTING_KEY_USER);
    }

    @Override
    public void sendEventCelebrity(String text, Long postId, Long authorId) {
        sendInternal(text, postId, authorId, ROUTING_KEY_CELEBRITY);
    }

    private void sendInternal(String text, Long postId, Long authorId, String routingKey) {
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME,
                routingKey.replace("*", String.valueOf(authorId)),
                text,
                message -> {
                    message.getMessageProperties().setHeader("postId", postId);
                    return message;
                }
        );
    }

}
