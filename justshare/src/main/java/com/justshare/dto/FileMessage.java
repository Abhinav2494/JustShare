package com.justshare.dto;

public class FileMessage {

    private String roomCode;
    private Long fileId;
    private String fileName;
    private Long fileSize;
    private String fileUrl;

    public FileMessage() {
    }

    public FileMessage(
            String roomCode,
            Long fileId,
            String fileName,
            Long fileSize,
            String fileUrl) {

        this.roomCode = roomCode;
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}