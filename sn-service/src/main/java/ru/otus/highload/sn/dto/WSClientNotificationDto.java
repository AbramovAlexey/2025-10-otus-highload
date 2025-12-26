package ru.otus.highload.sn.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WSClientNotificationDto {

    Long authorId;
    Long subscriberId;
    String content;

}
