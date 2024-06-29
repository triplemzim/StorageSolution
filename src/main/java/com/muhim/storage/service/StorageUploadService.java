package com.muhim.storage.service;

import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * File upload service
 */
public interface StorageUploadService {
    FileMetadataDTO saveFile(String user,
                             MultipartFile file,
                             FileVisibility visibility,
                             List<String> tags, String request) throws IOException;
}
