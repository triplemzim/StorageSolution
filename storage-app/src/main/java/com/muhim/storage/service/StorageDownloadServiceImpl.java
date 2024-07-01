package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * StorageDownloadService implementation
 *
 * @author muhim
 */
@Service
public class StorageDownloadServiceImpl implements StorageDownloadService {

    private final FileMetaDataRepository fileMetaDataRepository;

    private final GridFsClient gridFsClient;

    @Autowired
    public StorageDownloadServiceImpl(GridFsClient gridFsClient, FileMetaDataRepository repository) {
        this.gridFsClient = gridFsClient;
        this.fileMetaDataRepository = repository;
    }

    @Override
    public InputStream downloadFileStream(String id) {
        ObjectId objectId = new ObjectId(id);
        if (!fileMetaDataRepository.existsByFileId(objectId)) {
            throw new IllegalArgumentException("File does not exist!");
        }
        return gridFsClient.openDownloadStream(objectId);
    }

    @Override
    public String getOriginalFilename(String id) {
        ObjectId objectId = new ObjectId(id);
        FileMetadata fileMetadata = fileMetaDataRepository.findByFileId(objectId);
        return fileMetadata.getFilename();
    }
}
