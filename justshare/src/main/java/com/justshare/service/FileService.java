package com.justshare.service;

import com.justshare.entity.Room;
import com.justshare.entity.SharedFile;
import com.justshare.repository.RoomRepository;
import com.justshare.repository.SharedFileRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final SharedFileRepository fileRepository;
    private final RoomRepository roomRepository;

    private final Path uploadDirectory =
            Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public FileService(
            SharedFileRepository fileRepository,
            RoomRepository roomRepository
    ) {

        this.fileRepository = fileRepository;
        this.roomRepository = roomRepository;

        try {

            Files.createDirectories(
                    uploadDirectory
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not create upload directory",
                    e
            );
        }
    }

    // ==================================================
    // UPLOAD FILE
    // ==================================================

    public SharedFile uploadFile(
            String roomCode,
            MultipartFile file
    ) throws IOException {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );

        if (file == null ||
                file.isEmpty()) {

            throw new RuntimeException(
                    "Cannot upload empty file"
            );
        }

        String originalName =
                file.getOriginalFilename();

        if (originalName == null ||
                originalName.trim().isEmpty()) {

            originalName = "unknown-file";
        }

        // ==================================================
        // GET FILE EXTENSION
        // ==================================================

        String extension = "";

        int lastDot =
                originalName.lastIndexOf(".");

        if (lastDot >= 0) {

            extension =
                    originalName.substring(
                            lastDot
                    );
        }

        // ==================================================
        // GENERATE UNIQUE FILE NAME
        // ==================================================

        String storedName =
                UUID.randomUUID() +
                        extension;

        Path destination =
                uploadDirectory
                        .resolve(storedName)
                        .normalize();

        // Prevent path traversal
        if (!destination.startsWith(
                uploadDirectory
        )) {

            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        // ==================================================
        // SAVE FILE
        // ==================================================

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        // ==================================================
        // CREATE DATABASE ENTITY
        // ==================================================

        SharedFile sharedFile =
                new SharedFile();

        sharedFile.setOriginalName(
                originalName
        );

        sharedFile.setStoredName(
                storedName
        );

        sharedFile.setContentType(
                file.getContentType()
        );

        sharedFile.setFileSize(
                file.getSize()
        );

        sharedFile.setFilePath(
                destination.toString()
        );

        sharedFile.setUploadedAt(
                LocalDateTime.now()
        );

        sharedFile.setRoom(
                room
        );

        return fileRepository.save(
                sharedFile
        );
    }

    // ==================================================
    // GET ALL FILES
    // ==================================================

    public List<SharedFile> getFiles(
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

        return fileRepository.findByRoom(
                room
        );
    }

    // ==================================================
    // GET SINGLE FILE
    // ==================================================


    public SharedFile getFile(
            String roomCode,
            Long fileId
    ) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );

        return fileRepository
                .findById(fileId)
                .filter(file ->
                        file.getRoom() != null &&
                                file.getRoom().getId() != null &&
                                file.getRoom().getId().equals(room.getId())
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "File not found"
                        )
                );
    }



    // ==================================================

    public Resource loadFile(
            String roomCode,
            Long fileId
    ) throws IOException {

        SharedFile file =
                getFile(
                        roomCode,
                        fileId
                );

        Path path =
                Paths.get(
                        file.getFilePath()
                ).normalize();

        // Security check
        if (!path.startsWith(
                uploadDirectory
        )) {

            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        Resource resource =
                new UrlResource(
                        path.toUri()
                );

        if (!resource.exists() ||
                !resource.isReadable()) {

            throw new RuntimeException(
                    "File does not exist or cannot be read"
            );
        }

        return resource;
    }
}

