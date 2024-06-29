package com.muhim.storage.service;

import java.io.InputStream;

public interface StorageDownloadService {
    InputStream downloadFileStream(String id);

    String getOriginalFilename(String id);
}
