package ru.otus.highload.sn.dao;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import ru.otus.highload.sn.dto.DialogMessage;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.highload.sn.config.RedisConfig.INDEX_KEY_PREFIX;
import static ru.otus.highload.sn.config.RedisConfig.KEY_PREFIX;

@Component
@ConditionalOnProperty(name = "dialog.storage.type", havingValue = "redis")
@Slf4j
@RequiredArgsConstructor
public class DialogDaoRedisImpl implements DialogDao {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisAtomicLong idCounter;
    private final RedisScript<String> insertScript;
    private final RedisScript<List> selectScript;

    @PostConstruct
    public void postConstruct() {
        log.info("Using redis storage for dialogs");
    }

    @Override
    public Long addMessage(Long fromId, Long toId, String content) {
        Long id = idCounter.incrementAndGet();
        List<String> keys = new ArrayList<>();
        keys.add("%s:%s".formatted(KEY_PREFIX, id));
        keys.add("%s:%s".formatted(INDEX_KEY_PREFIX, getConversationId(fromId, toId)));
        var args = new ArrayList<>();
        args.add("fromId");
        args.add(fromId);
        args.add("toId");
        args.add(toId);
        args.add("content");
        args.add(content);

        redisTemplate.execute(insertScript, keys, args.toArray());
        return id;
    }

    @Override
    public List<DialogMessage> getMessages(Long fromId, Long toId) {
        var keys = new ArrayList<String>();
        keys.add("%s:%s".formatted(INDEX_KEY_PREFIX, getConversationId(fromId, toId)));
        keys.add(KEY_PREFIX);
        List values = redisTemplate.execute(selectScript, keys);
        var dialogMessages = new ArrayList<DialogMessage>();
        values.forEach(row -> {
            var currentList = (List<String>) row;
            var dialogMessage = new DialogMessage();
            for (int i = 0; i < currentList.size(); i+=2) {
                switch (currentList.get(i)) {
                    case "fromId" -> dialogMessage.setFrom(currentList.get(i+1));
                    case "toId" -> dialogMessage.setTo(currentList.get(i+1));
                    case "content" -> dialogMessage.setText(currentList.get(i+1));
                }
            }
            dialogMessages.add(dialogMessage);
        });
        return dialogMessages;
    }

}
