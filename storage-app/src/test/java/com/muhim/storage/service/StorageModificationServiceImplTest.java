package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StorageModificationServiceImplTest {
    @Mock
    private FileMetaDataRepository fileMetaDataRepository;

    @Mock
    private GridFsClient gridFsClient;

    @InjectMocks
    private StorageModificationServiceImpl storageModificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteFilesByUserAndName_shouldDeleteFileSuccessfully() {
        // Arrange
        String user = "testuser";
        String filename = "testfile.txt";
        ObjectId fileId = new ObjectId("507f1f77bcf86cd799439011");

        FileMetadata mockMetadata = FileMetadata.builder()
                .user(user)
                .filename(filename)
                .fileId(fileId)
                .build();

        when(fileMetaDataRepository.existsByUserAndFilename(user, filename)).thenReturn(true);
        when(fileMetaDataRepository.findByUserAndFilename(user, filename)).thenReturn(mockMetadata);

        // Act
        storageModificationService.deleteFilesByUserAndName(user, filename);

        // Assert
        verify(fileMetaDataRepository, times(1)).existsByUserAndFilename(user, filename);
        verify(fileMetaDataRepository, times(1)).findByUserAndFilename(user, filename);
        verify(gridFsClient, times(1)).delete(fileId);
        verify(fileMetaDataRepository, times(1)).delete(mockMetadata);
    }

    @Test
    void deleteFilesByUserAndName_shouldThrowExceptionIfFileNotExists() {
        // Arrange
        String user = "testuser";
        String filename = "testfile.txt";

        when(fileMetaDataRepository.existsByUserAndFilename(filename, user)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                storageModificationService.deleteFilesByUserAndName(user, filename));

        verify(fileMetaDataRepository, times(1)).existsByUserAndFilename(user, filename);
        verify(fileMetaDataRepository, never()).findByUserAndFilename(anyString(), anyString());
        verify(gridFsClient, never()).delete(any());
        verify(fileMetaDataRepository, never()).delete(any());
    }

    @Test
    void renameFile_shouldRenameFileSuccessfully() {
        // Arrange
        String user = "testuser";
        String filename = "testfile.txt";
        String newFilename = "newfile.txt";

        FileMetadata mockMetadata = FileMetadata.builder()
                .user(user)
                .filename(filename)
                .build();

        when(fileMetaDataRepository.existsByUserAndFilename(user, filename)).thenReturn(true);
        when(fileMetaDataRepository.findByUserAndFilename(user, filename)).thenReturn(mockMetadata);

        // Act
        storageModificationService.renameFile(user, filename, newFilename);

        // Assert
        verify(fileMetaDataRepository, times(1)).existsByUserAndFilename(user, filename);
        verify(fileMetaDataRepository, times(1)).findByUserAndFilename(user, filename);
        verify(fileMetaDataRepository, times(1)).save(mockMetadata);
    }

    @Test
    void renameFile_shouldThrowExceptionIfFileNotExists() {
        // Arrange
        String user = "testuser";
        String filename = "testfile.txt";
        String newFilename = "newfile.txt";

        when(fileMetaDataRepository.existsByUserAndFilename(user, filename)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                storageModificationService.renameFile(user, filename, newFilename));

        verify(fileMetaDataRepository, times(1)).existsByUserAndFilename(user, filename);
        verify(fileMetaDataRepository, never()).findByUserAndFilename(anyString(), anyString());
        verify(fileMetaDataRepository, never()).save(any());
    }
}