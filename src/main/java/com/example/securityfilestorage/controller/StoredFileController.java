package com.example.securityfilestorage.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.securityfilestorage.exception.FileNotFoundException;
import com.example.securityfilestorage.model.StoredFile;
import com.example.securityfilestorage.service.FileStorageService;

@RestController
@RequestMapping("/files")
public class StoredFileController {

	@Autowired
	private FileStorageService fileStorageService;
	
	@PostMapping("/upload")
	public ResponseEntity<StoredFile> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			StoredFile storedFile = fileStorageService.uploadFile(file);
			return new ResponseEntity<>(storedFile, HttpStatus.CREATED);
		} catch (IOException | GeneralSecurityException e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<StoredFile> getFileMetadata(@PathVariable Long id) {
		try {
			StoredFile storedFile = fileStorageService.getFile(id);
			return new ResponseEntity<>(storedFile, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) {
		try {
			StoredFile storedFile = fileStorageService.getFile(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			fileStorageService.downloadEncryptedFile(id, baos);
			
			InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
			
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + storedFile.getName());
			
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch (IOException | GeneralSecurityException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (FileNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
}
