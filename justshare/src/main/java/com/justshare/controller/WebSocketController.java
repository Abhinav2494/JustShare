package com.justshare.controller;

import com.justshare.dto.TextMessage;
import com.justshare.dto.TextResponse;
import com.justshare.entity.SharedText;
import com.justshare.service.TextService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final TextService textService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(
            TextService textService,
            SimpMessagingTemplate messagingTemplate) {

        this.textService = textService;
        this.messagingTemplate =
                messagingTemplate;
    }

    @MessageMapping("/text")
    public void sendText(
            TextMessage message) {

        SharedText text =
                textService.saveText(
                        message.getRoomCode(),
                        message.getContent()
                );

        TextResponse response =
                new TextResponse(text);

        messagingTemplate.convertAndSend(
                "/topic/room/"
                        + message.getRoomCode(),
                response
        );
    }
}