package com.justshare.dto;

import com.justshare.entity.SharedFile;

import java.time.LocalDateTime;

public class FileResponse {

    private Long id;
    private String originalName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    public FileResponse(SharedFile file) {

        this.id = file.getId();
        this.originalName = file.getOriginalName();
        this.contentType = file.getContentType();
        this.fileSize = file.getFileSize();
        this.uploadedAt = file.getUploadedAt();
    }

    public Long getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}