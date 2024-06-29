package com.muhim.storage.service;

import org.springframework.transaction.annotation.Transactional;

public interface StorageModificationService {
    @Transactional
    void deleteFilesByUserAndName(String user, String filename);

    void renameFile(String user, String filename, String newFilename);
}
