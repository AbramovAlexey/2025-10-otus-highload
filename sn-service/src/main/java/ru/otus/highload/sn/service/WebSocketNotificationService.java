package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.otus.highload.api.dto.WSClientNotificationDto;

import static ru.otus.highload.sn.config.WebSocketConfig.CHANNEL_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendPostNotification(WSClientNotificationDto notificationDto) {
        messagingTemplate.convertAndSend("/topic/u-%s%s".formatted(notificationDto.getSubscriberId().toString(), CHANNEL_NAME),
                                         notificationDto);
    }

}
