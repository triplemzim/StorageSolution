package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    @Mock
    private Tika tika;

    @InjectMocks
    private StorageUploadServiceImpl storageUploadServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFile_shouldSaveFileMetadata() throws IOException {
        // Arrange
        String user = "testUser";
        String filename = "testFile.txt";
        String contentType = "text/plain";
        String baseUrl = "localhost:8080";
        FileVisibility visibility = FileVisibility.PUBLIC;
        List<String> tags = List.of("tag1", "tag2");
        ObjectId objectId = new ObjectId();

        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(gridFsClient.store(any(), any(), any())).thenReturn(objectId);

        FileMetadata fileMetadata = FileMetadata.builder()
                .user(user)
                .filename(filename)
                .fileId(objectId)
                .visibility(visibility)
                .tags(tags.stream().sorted().toList())
                .fileSize(file.getSize())
                .contentType(contentType)
                .uploadDate(new Date())
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
        assertThrows(IllegalArgumentException.class, () -> {
            storageUploadServiceImpl.saveFile(user, file, visibility, tags, baseUrl);
        });
    }

}