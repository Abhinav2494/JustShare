package com.justshare.dto;

public class CreateRoomResponse {

    private String roomCode;

    public CreateRoomResponse(
            String roomCode
    ) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return roomCode;
    }
}
