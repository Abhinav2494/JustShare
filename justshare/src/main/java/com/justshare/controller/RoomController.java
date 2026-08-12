package com.justshare.controller;

import com.justshare.dto.CreateRoomResponse;
import com.justshare.entity.Room;
import com.justshare.service.RoomService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(
            RoomService roomService
    ) {
        this.roomService = roomService;
    }

    // =========================================================
    // CREATE ROOM
    // =========================================================

    /*
     * POST
     * /api/rooms
     */

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom() {

        CreateRoomResponse response =
                roomService.createRoom();

        return ResponseEntity.ok(
                response
        );
    }

    // =========================================================
    // GET ROOM
    // =========================================================

    /*
     * GET
     * /api/rooms/{roomCode}
     */

    @GetMapping("/{roomCode}")
    public ResponseEntity<Room> getRoom(
            @PathVariable String roomCode
    ) {

        return roomService
                .getRoom(roomCode)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }
}
