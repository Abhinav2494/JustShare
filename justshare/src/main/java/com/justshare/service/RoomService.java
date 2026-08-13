package com.justshare.service;

import com.justshare.dto.CreateRoomResponse;
import com.justshare.entity.Room;
import com.justshare.repository.RoomRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random =
            new SecureRandom();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RoomService(
            RoomRepository roomRepository
    ) {
        this.roomRepository = roomRepository;
    }

    // =========================================================
    // CREATE ROOM
    // =========================================================

    public CreateRoomResponse createRoom() {

        String roomCode =
                generateUniqueRoomCode();

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime expiry =
                now.plusDays(7);

        Room room =
                new Room(
                        roomCode,
                        now,
                        expiry
                );

        roomRepository.save(room);

        return new CreateRoomResponse(
                roomCode
        );
    }

    // =========================================================
    // GET ROOM
    // =========================================================

    public Optional<Room> getRoom(
            String roomCode
    ) {

        return roomRepository.findByRoomCode(
                roomCode
        );
    }

    // =========================================================
    // GENERATE UNIQUE ROOM CODE
    // =========================================================

    private String generateUniqueRoomCode() {

        String code;

        do {

            code = generateCode(6);

        } while (
                roomRepository.existsByRoomCode(code)
        );

        return code;
    }

    // =========================================================
    // GENERATE RANDOM ROOM CODE
    // =========================================================

    private String generateCode(int length) {

        StringBuilder code =
                new StringBuilder();

        for (int i = 0; i < length; i++) {

            int index =
                    random.nextInt(
                            CHARACTERS.length()
                    );

            code.append(
                    CHARACTERS.charAt(index)
            );
        }

        return code.toString();
    }

    @Transactional
    public void deleteExpiredRooms() {

        LocalDateTime now = LocalDateTime.now();

        var expiredRooms =
                roomRepository.findByExpiresAtBefore(now);

        if (expiredRooms.isEmpty()) {
            return;
        }

        roomRepository.deleteAll(expiredRooms);

        System.out.println(
                "Deleted " +
                        expiredRooms.size() +
                        " expired rooms."
        );
    }
}
