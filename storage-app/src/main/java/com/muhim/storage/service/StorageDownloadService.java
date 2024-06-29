package com.muhim.storage.service;

import java.io.InputStream;

/**
 * File download service
 */
public interface StorageDownloadService {
    /**
     * Return stream to start downloading the file
     *
     * @param id file id
     * @return inputStream
     */
    InputStream downloadFileStream(String id);

    /**
     * Get original filename to set before download as
     * filename could be changed when storing in database
     *
     * @param id file id
     * @return a string
     */
    String getOriginalFilename(String id);
}
