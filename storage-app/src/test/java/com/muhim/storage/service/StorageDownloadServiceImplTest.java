package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
import com.muhim.storage.repository.FileMetaDataRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StorageDownloadServiceImplTest {


    @Mock
    private FileMetaDataRepository fileMetaDataRepository;

    @Mock
    private GridFsClient gridFsClient;

    @InjectMocks
    private StorageDownloadServiceImpl storageDownloadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void downloadFileStream_shouldReturnInputStream() {
        // Arrange
        String fileId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(fileId);
        byte[] testData = "Hello, World!".getBytes();
        InputStream mockInputStream = new ByteArrayInputStream(testData);

        when(fileMetaDataRepository.existsByFileId(objectId)).thenReturn(true);
        when(gridFsClient.openDownloadStream(objectId)).thenReturn(mockInputStream);

        // Act
        InputStream result = storageDownloadService.downloadFileStream(fileId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void downloadFileStream_shouldThrowExceptionIfFileDoesNotExist() {
        // Arrange
        String fileId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(fileId);

        when(fileMetaDataRepository.existsByFileId(objectId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> storageDownloadService.downloadFileStream(fileId));
    }

}