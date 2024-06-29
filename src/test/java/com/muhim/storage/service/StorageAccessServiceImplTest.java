package com.muhim.storage.service;

import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StorageAccessServiceImplTest {

    @Mock
    private FileMetaDataRepository fileMetaDataRepository;

    @InjectMocks
    private StorageAccessServiceImpl storageAccessService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPublicFiles_withoutTag_shouldReturnCorrectPage() {
        // Arrange
        int page = 0;
        String sortBy = "filename";
        String tag = null;
        String hexId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(hexId);
        String baseUrl = "http://localhost:8080/";

        List<FileMetadata> mockMetadataList = new ArrayList<>();
        mockMetadataList.add(FileMetadata.builder()
                .filename("file1")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag1"))
                .fileId(objectId)
                .build());
        mockMetadataList.add(FileMetadata.builder()
                .filename("file2")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag2"))
                .fileId(objectId)
                .build());

        Page<FileMetadata> mockPage = new PageImpl<>(mockMetadataList);

        when(fileMetaDataRepository.findByVisibility(eq(FileVisibility.PUBLIC), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<FileMetadataDTO> resultPage = storageAccessService.getPublicFiles(page, sortBy, tag, baseUrl);

        // Assert
        assertEquals(2, resultPage.getTotalElements());
        assertEquals("file1", resultPage.getContent().get(0).getFileName());
        assertEquals("file2", resultPage.getContent().get(1).getFileName());

        verify(fileMetaDataRepository, times(1))
                .findByVisibility(eq(FileVisibility.PUBLIC), any(Pageable.class));
    }

    @Test
    void getPublicFiles_withTag_shouldReturnCorrectPage() {
        // Arrange
        int page = 0;
        String sortBy = "filename";
        String tag = "lazy";
        String hexId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(hexId);
        String baseUrl = "http://localhost:8080/";

        List<FileMetadata> mockMetadataList = new ArrayList<>();
        mockMetadataList.add(FileMetadata.builder()
                .filename("file1")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag1"))
                .fileId(objectId)
                .build());
        mockMetadataList.add(FileMetadata.builder()
                .filename("file2")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag2"))
                .fileId(objectId)
                .build());

        Page<FileMetadata> mockPage = new PageImpl<>(mockMetadataList);

        when(fileMetaDataRepository.findByVisibilityAndTagsIgnoreCase(eq(FileVisibility.PUBLIC),
                eq(tag),
                any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<FileMetadataDTO> resultPage = storageAccessService.getPublicFiles(page, sortBy, tag, baseUrl);

        // Assert
        assertEquals(2, resultPage.getTotalElements());
        assertEquals("file1", resultPage.getContent().get(0).getFileName());
        assertEquals("file2", resultPage.getContent().get(1).getFileName());

        verify(fileMetaDataRepository, times(1))
                .findByVisibilityAndTagsIgnoreCase(eq(FileVisibility.PUBLIC), eq(tag), any(Pageable.class));
    }

    @Test
    void getUserFiles_withTag_shouldReturnCorrectPage() {
        // Arrange
        String user = "testuser";
        int page = 0;
        String hexId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(hexId);
        String sortBy = "filename";
        String tag = "tag1";
        String baseUrl = "http://localhost:8080/";

        List<FileMetadata> mockMetadataList = new ArrayList<>();
        mockMetadataList.add(FileMetadata.builder()
                .filename("file1")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag1"))
                .fileId(objectId)
                .build());
        mockMetadataList.add(FileMetadata.builder()
                .filename("file2")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag2"))
                .fileId(objectId)
                .build());

        Page<FileMetadata> mockPage = new PageImpl<>(mockMetadataList);

        when(fileMetaDataRepository.findByUserAndTagsIgnoreCase(eq(user), eq(tag), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<FileMetadataDTO> resultPage = storageAccessService.getUserFiles(user, page, sortBy, tag, baseUrl);

        // Assert
        assertEquals(2, resultPage.getTotalElements());
        assertEquals("file1", resultPage.getContent().get(0).getFileName());
        assertEquals("file2", resultPage.getContent().get(1).getFileName());

        verify(fileMetaDataRepository, times(1))
                .findByUserAndTagsIgnoreCase(eq(user), eq(tag), any(Pageable.class));
    }

    @Test
    void getUserFiles_withoutTag_shouldReturnCorrectPage() {
        // Arrange
        String user = "testuser";
        int page = 0;
        String hexId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(hexId);
        String sortBy = "filename";
        String tag = "";
        String baseUrl = "http://localhost:8080/";

        List<FileMetadata> mockMetadataList = new ArrayList<>();
        mockMetadataList.add(FileMetadata.builder()
                .filename("file1")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag1"))
                .fileId(objectId)
                .build());
        mockMetadataList.add(FileMetadata.builder()
                .filename("file2")
                .visibility(FileVisibility.PUBLIC)
                .tags(List.of("tag2"))
                .fileId(objectId)
                .build());

        Page<FileMetadata> mockPage = new PageImpl<>(mockMetadataList);

        when(fileMetaDataRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<FileMetadataDTO> resultPage = storageAccessService.getUserFiles(user, page, sortBy, tag, baseUrl);

        // Assert
        assertEquals(2, resultPage.getTotalElements());
        assertEquals("file1", resultPage.getContent().get(0).getFileName());
        assertEquals("file2", resultPage.getContent().get(1).getFileName());

        verify(fileMetaDataRepository, times(1))
                .findByUser(eq(user), any(Pageable.class));
    }
}
