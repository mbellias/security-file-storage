package com.example.securityfilestorage.exception;

@SuppressWarnings("serial")
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}