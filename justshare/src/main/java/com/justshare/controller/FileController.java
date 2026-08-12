package com.justshare.controller;

import com.justshare.dto.FileResponse;
import com.justshare.entity.SharedFile;
import com.justshare.service.FileService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/api/rooms/{roomCode}/files")
public class FileController {

    private final FileService fileService;
    private final SimpMessagingTemplate messagingTemplate;

    public FileController(
            FileService fileService,
            SimpMessagingTemplate messagingTemplate) {

        this.fileService = fileService;
        this.messagingTemplate = messagingTemplate;
    }

    // =========================================================
    // UPLOAD FILE
    // =========================================================

    /*
     * POST
     * /api/rooms/A7K29P/files
     */

    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(
            @PathVariable String roomCode,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        SharedFile uploadedFile =
                fileService.uploadFile(
                        roomCode,
                        file
                );

        FileResponse response =
                new FileResponse(
                        uploadedFile
                );

        /*
         * Notify all users in this room
         * about the newly uploaded file.
         */

        messagingTemplate.convertAndSend(
                "/topic/room/" +
                        roomCode +
                        "/files",
                response
        );

        return ResponseEntity.ok(
                response
        );
    }

    // =========================================================
    // GET ALL FILES
    // =========================================================

    /*
     * GET
     * /api/rooms/A7K29P/files
     */

    @GetMapping
    public ResponseEntity<List<FileResponse>> getFiles(
            @PathVariable String roomCode
    ) {

        List<FileResponse> files =
                fileService
                        .getFiles(roomCode)
                        .stream()
                        .map(FileResponse::new)
                        .toList();

        return ResponseEntity.ok(
                files
        );
    }

    // =========================================================
    // DOWNLOAD FILE
    // =========================================================

    /*
     * GET
     * /api/rooms/A7K29P/files/1/download
     */

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String roomCode,
            @PathVariable Long fileId
    ) throws IOException {

        SharedFile file =
                fileService.getFile(
                        roomCode,
                        fileId
                );

        Resource resource =
                fileService.loadFile(
                        roomCode,
                        fileId
                );

        String contentType =
                file.getContentType();

        if (contentType == null ||
                contentType.isBlank()) {

            contentType =
                    "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                file.getOriginalName() +
                                "\""
                )
                .body(resource);
    }
}
