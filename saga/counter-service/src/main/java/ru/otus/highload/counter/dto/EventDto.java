package ru.otus.highload.counter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class EventDto {

    private Long id;
    @JsonProperty("message_key")
    private String messageKey;
    private String payload;
    @JsonProperty("created_at")
    private Long createdAt;

}
