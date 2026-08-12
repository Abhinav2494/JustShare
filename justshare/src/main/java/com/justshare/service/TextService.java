 package com.justshare.service;

import com.justshare.entity.Room;
import com.justshare.entity.SharedText;
import com.justshare.repository.RoomRepository;
import com.justshare.repository.SharedTextRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TextService {

    private final SharedTextRepository textRepository;
    private final RoomRepository roomRepository;

    public TextService(
            SharedTextRepository textRepository,
            RoomRepository roomRepository
    ) {
        this.textRepository = textRepository;
        this.roomRepository = roomRepository;
    }

    // ==================================================
    // GET ALL TEXTS
    // ==================================================

    public List<SharedText> getTexts(String roomCode) {

        Room room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );

        return textRepository.findByRoom(room);
    }

    // ==================================================
    // SAVE TEXT
    // ==================================================

    public SharedText saveText(
            String roomCode,
            String content
    ) {

        Room room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );

        if (content == null ||
                content.trim().isEmpty()) {

            throw new RuntimeException(
                    "Text cannot be empty"
            );
        }

        SharedText text = new SharedText();

        text.setContent(
                content.trim()
        );

        text.setCreatedAt(
                LocalDateTime.now()
        );

        text.setRoom(room);

        return textRepository.save(text);
    }
}
