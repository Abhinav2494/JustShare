package com.justshare.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(nullable = false)
    private boolean compressed;

    @Column(name = "compressed_size")
    private Long compressedSize;

    /*
     * Example:
     *
     * abc-123.pdf.gz
     */
    @Column(name = "stored_name", nullable = false, unique = true)
    private String storedName;


    @Column(name = "content_type")
    private String contentType;


    /*
     * Original file size.
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;


    /*
     * Size after GZIP compression.
     */


    @Column(name = "file_path", nullable = false)
    private String filePath;


    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;


    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public SharedFile() {
    }


    // ==================================================
    // GETTERS
    // ==================================================

    public Long getId() {
        return id;
    }


    public String getOriginalName() {
        return originalName;
    }


    public String getStoredName() {
        return storedName;
    }


    public String getContentType() {
        return contentType;
    }


    public Long getFileSize() {
        return fileSize;
    }


    public Long getCompressedSize() {
        return compressedSize;
    }


    public String getFilePath() {
        return filePath;
    }


    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }


    public boolean isCompressed() {
        return compressed;
    }


    public Room getRoom() {
        return room;
    }


    // ==================================================
    // SETTERS
    // ==================================================

    public void setOriginalName(
            String originalName
    ) {
        this.originalName = originalName;
    }


    public void setStoredName(
            String storedName
    ) {
        this.storedName = storedName;
    }


    public void setContentType(
            String contentType
    ) {
        this.contentType = contentType;
    }


    public void setFileSize(
            Long fileSize
    ) {
        this.fileSize = fileSize;
    }


    public void setCompressedSize(
            Long compressedSize
    ) {
        this.compressedSize =
                compressedSize;
    }


    public void setFilePath(
            String filePath
    ) {
        this.filePath = filePath;
    }


    public void setUploadedAt(
            LocalDateTime uploadedAt
    ) {
        this.uploadedAt = uploadedAt;
    }


    public void setCompressed(
            boolean compressed
    ) {
        this.compressed = compressed;
    }


    public void setRoom(Room room) {
        this.room = room;
    }
}