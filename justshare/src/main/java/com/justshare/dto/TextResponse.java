package com.justshare.dto;

import com.justshare.entity.SharedText;
import com.justshare.service.EncryptionService;

import java.time.LocalDateTime;

public class TextResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public TextResponse(
            SharedText text,
            EncryptionService encryptionService
    ) {
        this.id = text.getId();

        /*
         * Decrypt + decompress only when
         * preparing the response.
         */
        this.content =
                text.getContent(
                        encryptionService
                );

        this.createdAt =
                text.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
