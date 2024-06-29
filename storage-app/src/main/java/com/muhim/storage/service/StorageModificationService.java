package com.muhim.storage.service;


/**
 * File modification and deletion service
 */
public interface StorageModificationService {
    void deleteFilesByUserAndName(String user, String filename);

    void renameFile(String user, String filename, String newFilename);
}
