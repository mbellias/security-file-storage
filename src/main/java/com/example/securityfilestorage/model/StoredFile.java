package com.example.securityfilestorage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;     // Original name of the file
    private String s3Key;    // Key of the file in S3
    private String url;      // Public URL to access the file
    private String contentType; // MIME type of the file
    private long size;       // Size of the file in bytes
    private String username;

    // Default constructor
    public StoredFile() {
    }

    // Parameterized constructor
    public StoredFile(String name, String s3Key, String url, String contentType, long size, String username) {
        this.name = name;
        this.s3Key = s3Key;
        this.url = url;
        this.contentType = contentType;
        this.size = size;
        this.username = username;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}