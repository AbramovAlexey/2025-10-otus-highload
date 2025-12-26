package ru.otus.highload.sn.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.otus.highload.sn.dao.UserDao;
import ru.otus.highload.sn.dto.WSClientNotificationDto;
import ru.otus.highload.sn.service.WebSocketNotificationService;

import static ru.otus.highload.sn.config.RabbitConfig.QUEUE_NAME_USER;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMessageListener {

    private final UserDao userDao;
    private final WebSocketNotificationService notificationService;

    @RabbitListener(queues = QUEUE_NAME_USER)
    public void listen(String message,
                       @Header("amqp_receivedRoutingKey") String routingKey,
                       @Header("postId") String postId) {
        log.info("Получено сообщение: {}{}{}", message, routingKey, postId);
        var authorId = extractUserIdFromRoutingKey(routingKey);
        userDao.getSubscribers(authorId)
                .forEach(subId -> notificationService.sendPostNotification(WSClientNotificationDto.builder()
                        .authorId(authorId)
                        .subscriberId(subId)
                        .content(message)
                        .build()));
    }

    private Long extractUserIdFromRoutingKey(String routingKey) {
        var parts = routingKey.split("\\.");
        return Long.valueOf(parts[1]);
    }

}
