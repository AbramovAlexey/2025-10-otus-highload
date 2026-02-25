package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.otus.highload.api.dto.DialogMessage;
import ru.otus.highload.api.grpc.DialogMessageDto;
import ru.otus.highload.api.grpc.DialogServiceGrpc;
import ru.otus.highload.api.grpc.GetMessagesRequest;
import ru.otus.highload.api.grpc.SendMessageRequest;
import ru.otus.highload.sn.dto.DialogMessageRestDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogServiceImpl implements DialogService {

    @GrpcClient("dialog-service")
    private DialogServiceGrpc.DialogServiceBlockingStub dialogStub;

    @Override
    public void addMessage(String userId, String userIdTo, DialogMessageRestDto dialogMessageRestDto) {
        log.info("Calling addMessage grpc");
        var messageDto = DialogMessageDto.newBuilder()
                .setText(dialogMessageRestDto.getText())
                .build();
        var request = SendMessageRequest.newBuilder()
                .setUserId(userId)
                .setUserIdTo(userIdTo)
                .setMessage(messageDto)
                .build();
        dialogStub.sendMessage(request);
    }

    @Override
    public List<DialogMessage> listMessages(String userId, String userIdTo) {
        log.info("Calling listMessage grpc");
        var request = GetMessagesRequest.newBuilder()
                .setUserId(userId)
                .setUserIdTo(userIdTo)
                .build();
        var response = dialogStub.getMessagesList(request);
        return response.getMessagesList()
                .stream()
                .map(message -> {
                    var dialogMessage = new DialogMessage();
                    dialogMessage.setTo(message.getTo());
                    dialogMessage.setFrom(message.getFrom());
                    dialogMessage.setText(message.getText());
                    return dialogMessage;
                })
                .collect(Collectors.toList());
    }

}
