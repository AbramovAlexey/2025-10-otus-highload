package ru.otus.highload.dms.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.otus.highload.api.grpc.*;
import ru.otus.highload.dms.dto.DialogMessageRestDto;
import ru.otus.highload.dms.service.DialogService;

import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class DialogGrpcService extends DialogServiceGrpc.DialogServiceImplBase {

    private final DialogService dialogService;

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        log.info("Received grpc request sendMessage");
        dialogService.addMessage(request.getUserId(), request.getUserIdTo(), new DialogMessageRestDto(request.getMessage().getText()));
        responseObserver.onNext(SendMessageResponse.newBuilder().setStatusMessage("OK").build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMessagesList(GetMessagesRequest request, StreamObserver<GetMessagesResponse> responseObserver) {
        log.info("Received grpc request listMessage");
        var messageList = dialogService.listMessages(request.getUserId(), request.getUserIdTo())
                                       .stream()
                                       .map(message -> DialogMessage.newBuilder()
                                                                    .setTo(message.getTo())
                                                                    .setFrom(message.getFrom())
                                                                    .setText(message.getText())
                                                                    .build())
                                       .collect(Collectors.toList());

        responseObserver.onNext(GetMessagesResponse.newBuilder()
                                                   .addAllMessages(messageList)
                                                   .build() );
        responseObserver.onCompleted();
    }

}
