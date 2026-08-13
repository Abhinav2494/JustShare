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
    private final EncryptionService encryptionService;

    public TextService(
            SharedTextRepository textRepository,
            RoomRepository roomRepository,
            EncryptionService encryptionService
    ) {
        this.textRepository = textRepository;
        this.roomRepository = roomRepository;
        this.encryptionService = encryptionService;
    }

    // ==================================================
    // SAVE TEXT
    // ==================================================

    public SharedText saveText(
            String roomCode,
            String content
    ) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );

        if (content == null ||
                content.trim().isEmpty()) {

            throw new RuntimeException(
                    "Text cannot be empty"
            );
        }

        SharedText text =
                new SharedText();

        /*
         * GZIP compression happens first.
         * AES-256-GCM encryption happens second.
         */
        text.setContent(
                content,
                encryptionService
        );

        text.setCreatedAt(
                LocalDateTime.now()
        );

        text.setRoom(room);

        return textRepository.save(text);
    }

    // ==================================================
    // GET TEXTS
    // ==================================================

    public List<SharedText> getTexts(
            String roomCode
    ) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );

        return textRepository.findByRoom(room);
    }

    // ==================================================
    // GET SINGLE TEXT
    // ==================================================

    public SharedText getText(
            String roomCode,
            Long textId
    ) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );

        return textRepository
                .findByIdAndRoom(
                        textId,
                        room
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Text not found"
                        )
                );
    }

    /*
     * Use this method when creating a response DTO.
     */
    public String getDecryptedContent(
            SharedText text
    ) {
        return text.getContent(encryptionService);
    }

    // ==================================================
    // DELETE TEXT
    // ==================================================

    public void deleteText(
            String roomCode,
            Long textId
    ) {

        SharedText text =
                getText(
                        roomCode,
                        textId
                );

        textRepository.delete(text);
    }
}
