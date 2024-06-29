package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageUploadServiceImplTest {


    @Mock
    GridFsClient gridFsClient = mock(GridFsClient.class);

    @Mock
    private FileMetaDataRepository fileMetaDataRepository;


    @Mock
    private MultipartFile file;

    @InjectMocks
    private StorageUploadServiceImpl storageUploadServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFile_shouldSaveFileMetadata() throws IOException, NoSuchAlgorithmException {
        // Arrange
        String user = "testUser";
        String filename = "testFile.txt";
        String contentType = "text/plain";
        String baseUrl = "localhost:8080";
        String fileRollingHash = "SHA256";
        FileVisibility visibility = FileVisibility.PUBLIC;
        List<String> tags = List.of("tag1", "tag2");
        ObjectId objectId = new ObjectId();

        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(gridFsClient.store(any(), any(), any())).thenReturn(objectId);
        String dummyString = "dummyString";
        InputStream inputStream = new ByteArrayInputStream(dummyString.getBytes());
        when(gridFsClient.openDownloadStream(any())).thenReturn(inputStream);

        FileMetadata fileMetadata = FileMetadata.builder()
                .user(user)
                .filename(filename)
                .fileId(objectId)
                .visibility(visibility)
                .tags(tags.stream().sorted().toList())
                .fileSize(file.getSize())
                .contentType(contentType)
                .uploadDate(new Date())
                .fileRollingHash(fileRollingHash)
                .build();

        when(fileMetaDataRepository.save(any(FileMetadata.class))).thenReturn(fileMetadata);

        // Act
        FileMetadataDTO result = storageUploadServiceImpl.saveFile(user, file, visibility, tags, baseUrl);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUploadDate());
        assertEquals(filename, result.getFileName());
        assertEquals(visibility, result.getVisibility());
        assertEquals(tags.stream().sorted().toList(), result.getTags());

        verify(fileMetaDataRepository, times(1)).save(any(FileMetadata.class));
    }

    @Test
    void saveFile_shouldThrowExceptionForInvalidTags() {
        // Arrange
        String user = "testUser";
        String baseUrl = "localhost:8080";
        FileVisibility visibility = FileVisibility.PUBLIC;
        List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                storageUploadServiceImpl.saveFile(user, file, visibility, tags, baseUrl));
    }

}