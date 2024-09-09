package com.example.securityfilestorage.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.securityfilestorage.model.StoredFile;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
	
	Optional<StoredFile> findByIdAndUsername(Long id, String username);
	
	List<StoredFile> findAllByUsername(String username);
}
