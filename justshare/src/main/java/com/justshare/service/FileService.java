package com.justshare.service;

import com.justshare.entity.Room;
import com.justshare.entity.SharedFile;
import com.justshare.repository.RoomRepository;
import com.justshare.repository.SharedFileRepository;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class FileService {

    private final SharedFileRepository fileRepository;
    private final RoomRepository roomRepository;
    private final EncryptionService encryptionService;

    private final Path uploadDirectory =
            Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

    public FileService(
            SharedFileRepository fileRepository,
            RoomRepository roomRepository,
            EncryptionService encryptionService
    ) {
        this.fileRepository = fileRepository;
        this.roomRepository = roomRepository;
        this.encryptionService = encryptionService;

        try {
            Files.createDirectories(uploadDirectory);
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
                                new RuntimeException("Room not found")
                        );

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Cannot upload empty file"
            );
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null ||
                originalName.trim().isEmpty()) {
            originalName = "unknown-file";
        }

        /*
         * Final file is encrypted.
         *
         * Compression happens first.
         * Encryption happens second.
         */
        String storedName =
                UUID.randomUUID() + ".enc";

        Path destination =
                uploadDirectory
                        .resolve(storedName)
                        .normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        long originalSize = file.getSize();

        /*
         * 1. Read original file.
         * 2. GZIP compress it in memory.
         * 3. AES-256-GCM encrypt the compressed bytes.
         * 4. Save encrypted bytes to disk.
         */
        byte[] compressedBytes;

        try (
                InputStream input = file.getInputStream();
                ByteArrayOutputStream compressedOutput =
                        new ByteArrayOutputStream();
                GZIPOutputStream gzip =
                        new GZIPOutputStream(compressedOutput)
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                gzip.write(buffer, 0, bytesRead);
            }

            gzip.finish();
            compressedBytes = compressedOutput.toByteArray();
        }

        byte[] encryptedBytes =
                encryptionService.encrypt(compressedBytes);

        Files.write(destination, encryptedBytes);

        /*
         * This is the final encrypted file size.
         */
        long encryptedSize =
                Files.size(destination);

        SharedFile sharedFile =
                new SharedFile();

        sharedFile.setOriginalName(originalName);
        sharedFile.setStoredName(storedName);
        sharedFile.setContentType(file.getContentType());
        sharedFile.setFileSize(originalSize);

        /*
         * Store the final on-disk encrypted size.
         */
        sharedFile.setCompressedSize(encryptedSize);

        sharedFile.setFilePath(destination.toString());
        sharedFile.setUploadedAt(LocalDateTime.now());

        /*
         * true means the stored data is compressed
         * and encrypted.
         */
        sharedFile.setCompressed(true);

        sharedFile.setRoom(room);

        return fileRepository.save(sharedFile);
    }

    // ==================================================
    // GET ALL FILES
    // ==================================================

    public List<SharedFile> getFiles(String roomCode) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException("Room not found")
                        );

        return fileRepository.findByRoom(room);
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
                                new RuntimeException("Room not found")
                        );

        return fileRepository
                .findByIdAndRoom(fileId, room)
                .orElseThrow(() ->
                        new RuntimeException("File not found")
                );
    }

    // ==================================================
    // LOAD / DECRYPT / DECOMPRESS FILE
    // ==================================================

    public Resource loadFile(
            String roomCode,
            Long fileId
    ) throws IOException {

        SharedFile file =
                getFile(roomCode, fileId);

        Path path =
                Paths.get(file.getFilePath())
                        .normalize();

        if (!path.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        if (!Files.exists(path)) {
            throw new RuntimeException(
                    "File does not exist"
            );
        }

        /*
         * Current files:
         *
         * encrypted
         *     ↓
         * decrypt
         *     ↓
         * compressed
         *     ↓
         * GZIP decompress
         *     ↓
         * original file
         */
        if (file.isCompressed()) {

            byte[] encryptedBytes =
                    Files.readAllBytes(path);

            byte[] compressedBytes =
                    encryptionService.decrypt(
                            encryptedBytes
                    );

            byte[] originalBytes;

            try (
                    GZIPInputStream gzip =
                            new GZIPInputStream(
                                    new java.io.ByteArrayInputStream(
                                            compressedBytes
                                    )
                            );
                    ByteArrayOutputStream output =
                            new ByteArrayOutputStream();
            ) {
                gzip.transferTo(output);
                originalBytes = output.toByteArray();
            }

            return new InputStreamResource(
                    new java.io.ByteArrayInputStream(
                            originalBytes
                    )
            );
        }

        /*
         * Old / uncompressed file support.
         */
        return new InputStreamResource(
                Files.newInputStream(path)
        );
    }

    // ==================================================
    // DELETE FILE
    // ==================================================

    public void deleteFile(
            String roomCode,
            Long fileId
    ) throws IOException {

        SharedFile file =
                getFile(roomCode, fileId);

        Path path =
                Paths.get(file.getFilePath())
                        .normalize();

        if (!path.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        Files.deleteIfExists(path);

        fileRepository.delete(file);
    }
}
