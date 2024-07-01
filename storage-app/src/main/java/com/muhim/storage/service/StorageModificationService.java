package com.muhim.storage.service;


/**
 * File modification and deletion service
 *
 * @author muhim
 */
public interface StorageModificationService {
    /**
     * To delete the file from database
     *
     * @param user file belongs to the user
     * @param filename name of the file
     */
    void deleteFilesByUserAndName(String user, String filename);

    /**
     * To rename the file in database
     *
     * @param user the file belongs to the user
     * @param filename existing file name
     * @param newFilename enw file name to rename it
     */
    void renameFile(String user, String filename, String newFilename);
}
