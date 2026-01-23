package ru.otus.highload.sn.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.otus.highload.sn.dao.UserDao;
import ru.otus.highload.api.dto.WSClientNotificationDto;
import ru.otus.highload.sn.service.PostService;
import ru.otus.highload.sn.service.WebSocketNotificationService;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

import static ru.otus.highload.sn.config.RabbitConfig.QUEUE_NAME_CELEBRITY;
import static ru.otus.highload.sn.config.RabbitConfig.QUEUE_NAME_USER;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMessageListener {

    private final UserDao userDao;
    private final WebSocketNotificationService notificationService;
    private final PostService postService;

    //для авторов - обычных пользователей отправляем текст поста подписчикам сразу в уведомлении
    @RabbitListener(queues = QUEUE_NAME_USER, containerFactory = "rabbitListenerContainerFactory")
    public void listenUser(String message, @Header("amqp_receivedRoutingKey") String routingKey) {
        log.info("Получено сообщение user: {}-{}", message, routingKey);
        var authorId = extractUserIdFromRoutingKey(routingKey);
        userDao.getSubscribers(authorId)
                .forEach(subId -> notificationService.sendPostNotification(WSClientNotificationDto.builder()
                        .authorId(authorId)
                        .subscriberId(subId)
                        .content(message)
                        .rawData(true)
                        .build()));
    }

    //для "celebrity" ожидаем много постов, поэтому получаем пачкой
    //формируем ленту и отправляем уведомление подписчикам о новых данных
    //они уже через rest заберут все посты
    @RabbitListener(queues = QUEUE_NAME_CELEBRITY, containerFactory = "rabbitBatchListenerContainerFactory")
    public void listenCelebrity(List<Message> messages) {
        log.info("Получены сообщения celebrity size: {}", messages.size());
        var authors = new HashSet<Long>();
        messages.forEach(message -> {
            MessageProperties props = message.getMessageProperties();
            var authorId = extractUserIdFromRoutingKey(props.getReceivedRoutingKey());
            authors.add(authorId);
            var postId = (Long)props.getHeader("postId");
            var subscribers = userDao.getSubscribers(authorId);
            subscribers.forEach(subId -> postService.materialize(authorId, subId, postId, new String(message.getBody(), StandardCharsets.UTF_8)));

        });
        authors.forEach(authorId -> userDao.getSubscribers(authorId)
                                           .forEach(subId ->
                        notificationService.sendPostNotification(WSClientNotificationDto.builder()
                                .authorId(authorId)
                                .subscriberId(subId)
                                //отправляем не сами сообщения, а уведомление о том, что поступило
                                //много новых постов и их можно забрать через rest
                                .rawData(false)
                                .build())
                )
        );
    }

    private Long extractUserIdFromRoutingKey(String routingKey) {
        var parts = routingKey.split("\\.");
        return Long.valueOf(parts[1]);
    }

}
