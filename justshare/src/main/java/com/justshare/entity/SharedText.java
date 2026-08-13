package com.justshare.entity;

import com.justshare.service.EncryptionService;
import jakarta.persistence.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Entity
@Table(name = "shared_texts")
public class SharedText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Stored representation:
     *
     * original text
     *      ↓
     * GZIP compression
     *      ↓
     * AES-256-GCM encryption
     *      ↓
     * PostgreSQL bytea
     */
    @Column(
            name = "compressed_content",
            nullable = false,
            columnDefinition = "bytea"
    )
    private byte[] compressedContent;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /*
     * EncryptionService is not injected by JPA.
     * TextService sets the encrypted content using
     * setContent(content, encryptionService).
     */
    public SharedText() {
    }

    public Long getId() {
        return id;
    }

    /*
     * Decrypt + decompress.
     */
    @Transient
    public String getContent(EncryptionService encryptionService) {

        if (compressedContent == null) {
            return "";
        }

        try {
            byte[] compressedBytes =
                    encryptionService.decrypt(
                            compressedContent
                    );

            ByteArrayInputStream input =
                    new ByteArrayInputStream(
                            compressedBytes
                    );

            GZIPInputStream gzip =
                    new GZIPInputStream(input);

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            gzip.transferTo(output);

            return output.toString(
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not decompress text",
                    e
            );
        }
    }

    /*
     * Compress + encrypt.
     */
    public void setContent(
            String content,
            EncryptionService encryptionService
    ) {

        if (content == null) {
            content = "";
        }

        try {
            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            GZIPOutputStream gzip =
                    new GZIPOutputStream(output);

            gzip.write(
                    content.getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            gzip.finish();
            gzip.close();

            byte[] compressedBytes =
                    output.toByteArray();

            this.compressedContent =
                    encryptionService.encrypt(
                            compressedBytes
                    );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not compress text",
                    e
            );
        }
    }

    public byte[] getCompressedContent() {
        return compressedContent;
    }

    public void setCompressedContent(
            byte[] compressedContent
    ) {
        this.compressedContent =
                compressedContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
