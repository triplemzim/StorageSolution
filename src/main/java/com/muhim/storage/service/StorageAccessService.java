package com.muhim.storage.service;

import com.muhim.storage.dto.FileMetadataDTO;
import org.springframework.data.domain.Page;

public interface StorageAccessService {
    Page<FileMetadataDTO> getPublicFiles(int page, String sortBy, String tag, String request);

    Page<FileMetadataDTO> getUserFiles(String user,
                                       int page,
                                       String sortBy,
                                       String tag,
                                       String request);
}
