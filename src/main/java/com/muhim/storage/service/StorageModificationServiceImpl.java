package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageModificationServiceImpl implements StorageModificationService {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final GridFsClient gridFsClient;


    @Autowired
    public StorageModificationServiceImpl(GridFsClient gridFsClient, FileMetaDataRepository repository) {
        this.gridFsClient = gridFsClient;
        this.fileMetaDataRepository = repository;
    }

    @Override
    public void deleteFilesByUserAndName(String user, String filename) {
        if (!fileMetaDataRepository.existsByFilenameAndUser(filename, user)) {
            throw new IllegalArgumentException("File does not exist!");
        }
        FileMetadata fileMetadata = fileMetaDataRepository.findByFilenameAndUser(filename, user);
        gridFsClient.delete(fileMetadata.getFileId());
        fileMetaDataRepository.delete(fileMetadata);
    }

    @Override
    public void renameFile(String user, String filename, String newFilename) {
        if (!fileMetaDataRepository.existsByFilenameAndUser(filename, user)) {
            throw new IllegalArgumentException("File does not exist or you don't have write access to it!");
        }

        FileMetadata fileMetadata = fileMetaDataRepository.findByFilenameAndUser(filename, user);
        fileMetadata.setFilename(newFilename);
        fileMetaDataRepository.save(fileMetadata);
    }
}

