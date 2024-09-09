package com.example.securityfilestorage.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.securityfilestorage.exception.FileNotFoundException;
import com.example.securityfilestorage.model.StoredFile;
import com.example.securityfilestorage.repository.StoredFileRepository;
import com.example.securityfilestorage.security.FileEncryptor;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileStorageService {

	@Autowired
	private S3Client s3Client;
	
	@Value("${cloud.aws.s3.bucket:securityfilestorage}")
	private String bucketName;
	
	@Autowired
	private StoredFileRepository storedFileRepository;
	
    @Autowired
    private FileEncryptor fileEncryptor;
	
	public StoredFile uploadFile(MultipartFile file) throws IOException, GeneralSecurityException {
		String username = getAuthenticatedUsername();
		

        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
        fileEncryptor.encryptStream(file.getInputStream(), encryptedOutputStream);

        String keyName = username + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        byte[] encryptedBytes = encryptedOutputStream.toByteArray();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .acl("private")  // Set ACL as needed
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(encryptedBytes));

        String url = String.format("https://%s.s3.amazonaws.com/%s", bucketName, keyName);

        StoredFile storedFile = new StoredFile();
        storedFile.setName(file.getOriginalFilename());
        storedFile.setS3Key(keyName);
        storedFile.setUrl(url);
        storedFile.setContentType(file.getContentType());
        storedFile.setSize(file.getSize());
        storedFile.setUsername(username);

        return storedFileRepository.save(storedFile);
	}
	
	public StoredFile getFile(Long id) {
		String username = getAuthenticatedUsername();
	    return storedFileRepository.findById(id)
	    	.filter(file -> file.getUsername().equals(username))
	        .orElseThrow(() -> new FileNotFoundException("File with ID " + id + " not found"));
	}
	
    public void downloadEncryptedFile(Long fileId, OutputStream outputStream) throws IOException, GeneralSecurityException {
        StoredFile storedFile = getFile(fileId);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFile.getS3Key())
                .build();

        try (InputStream s3ObjectStream = s3Client.getObject(getObjectRequest)) {
            fileEncryptor.decryptStream(s3ObjectStream, outputStream);
        }
    }

	
	private String getAuthenticatedUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : "anonymous";
	}
	
}
