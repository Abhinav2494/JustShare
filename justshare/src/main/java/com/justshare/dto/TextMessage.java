package com.justshare.dto;

public class TextMessage {

    private String roomCode;
    private String content;

    public TextMessage() {
    }

    public TextMessage(
            String roomCode,
            String content) {

        this.roomCode = roomCode;
        this.content = content;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}