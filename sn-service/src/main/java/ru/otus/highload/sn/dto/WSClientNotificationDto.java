package ru.otus.highload.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WSClientNotificationDto {

    public Long authorId;
    public Long subscriberId;
    public String content;

    @Builder.Default
    public boolean rawData = false;

}
