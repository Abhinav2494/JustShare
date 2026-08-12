package com.justshare.dto;

import com.justshare.entity.SharedText;

import java.time.LocalDateTime;

public class TextResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public TextResponse(SharedText text) {
        this.id = text.getId();
        this.content = text.getContent();
        this.createdAt = text.getCreatedAt();
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