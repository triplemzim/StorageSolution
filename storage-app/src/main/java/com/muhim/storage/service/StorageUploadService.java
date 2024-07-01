package com.muhim.storage.service;

import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * File upload service
 *
 * @author muhim
 */
public interface StorageUploadService {

    /**
     * Upload file to database with given parameters
     *
     * @param user uploaded by the user
     * @param file the file to upload
     * @param visibility visibility settings
     * @param tags list of tags
     * @param baseUrl to dynamically create the download link
     * @return on successful upload - the file metadata with download link
     */
    FileMetadataDTO saveFile(String user,
                             MultipartFile file,
                             FileVisibility visibility,
                             List<String> tags, String baseUrl) throws IOException, NoSuchAlgorithmException;
}
