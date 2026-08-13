package com.justshare.controller;

import com.justshare.dto.TextMessage;
import com.justshare.dto.TextResponse;
import com.justshare.entity.SharedText;
import com.justshare.service.EncryptionService;
import com.justshare.service.TextService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final TextService textService;
    private final SimpMessagingTemplate messagingTemplate;
    private final EncryptionService encryptionService;

    public WebSocketController(
            TextService textService,
            SimpMessagingTemplate messagingTemplate,
            EncryptionService encryptionService
    ) {
        this.textService = textService;
        this.messagingTemplate = messagingTemplate;
        this.encryptionService = encryptionService;
    }

    @MessageMapping("/text")
    public void sendText(
            TextMessage message
    ) {

        SharedText text =
                textService.saveText(
                        message.getRoomCode(),
                        message.getContent()
                );

        TextResponse response =
                new TextResponse(
                        text,
                        encryptionService
                );

        messagingTemplate.convertAndSend(
                "/topic/room/"
                        + message.getRoomCode(),
                response
        );
    }
}
