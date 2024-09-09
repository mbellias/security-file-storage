package com.example.securityfilestorage.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;

public class FileEncryptor {

	private static final int IV_SIZE = 12;
	
	@Value("{aws.kms.key-id}")
	private String kmsKeyId;
	
	private final KmsClient kmsClient;
	
	public FileEncryptor(KmsClient kmsClient) {
		this.kmsClient = kmsClient;
	}
	
	public byte[] encryptData(byte[] data) {
		byte[] iv = new byte[IV_SIZE];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		
		EncryptRequest encryptRequest = EncryptRequest.builder()
				.keyId(kmsKeyId)
				.plaintext(SdkBytes.fromByteArray(data))
				.encryptionContext(Collections.singletonMap("purpose", "file-encryption"))
				.build();
		
		EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
		return encryptResponse.ciphertextBlob().asByteArray();
	}
	
	public byte[] decryptData(byte[] encryptedData) {
		DecryptRequest decryptRequest = DecryptRequest.builder()
				.ciphertextBlob(SdkBytes.fromByteArray(encryptedData))
				.encryptionContext(Collections.singletonMap("purpose", "file-encryption"))
				.build();
		
		return kmsClient.decrypt(decryptRequest).plaintext().asByteArray();
	}
	
	public void encryptStream(InputStream inputStream, OutputStream outputStream) throws IOException, GeneralSecurityException {
		byte[] data = inputStream.readAllBytes();
		byte[] encryptedData = encryptData(data);
		outputStream.write(encryptedData);
	}
	
	public void decryptStream(InputStream inputStream, OutputStream outputStream) throws IOException, GeneralSecurityException {
		byte[] encryptedData = inputStream.readAllBytes();
		byte[] decyptedData = decryptData(encryptedData);
		outputStream.write(decyptedData);
	}
	
}
