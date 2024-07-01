package com.muhim.storage.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.repository.FileMetaDataRepository;
import com.muhim.storage.helper.CustomPageImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for controller class providing tests for each endpoint
 * Prerequisite: A MongoDB instance is required which will be cleaned before running each test
 * DO NOT USE stating/production DB connection to run the tests
 *
 * @author muhim
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StorageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileMetaDataRepository fileMetadataRepository;

    private GridFSBucket gridFSBucket;

    @BeforeTestClass
    @Autowired
    public void beforeClass(MongoDatabaseFactory mongoDatabaseFactory) {
        gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
    }

    @BeforeEach
    public void setUp() throws Exception {
        fileMetadataRepository.deleteAll();
        GridFSFindIterable gridFSFiles = gridFSBucket.find();
        for (GridFSFile file : gridFSFiles) {
            gridFSBucket.delete(file.getObjectId());
        }
        /*
         * 10 files with file name -> file0.txt, file1.txt ...
         * Each file user is `test`
         * even index files are PUBLIC and odd index files are PRIVATE
         * tags for each file is `myTag0`, `myTag1` and so on
         */
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "file" + i + ".txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    ("This is a test file with index: " + i).getBytes()
            );

            mockMvc.perform(multipart("/api/storage/v1/files/upload")
                    .file(file)
                    .param("user", "test")
                    .param("visibility", i % 2 == 0 ? "PUBLIC" : "PRIVATE")
                    .param("tags", "myTag" + i, "myTag" + i * 2 + 1));
        }
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile3.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );

        mockMvc.perform(multipart("/api/storage/v1/files/upload")
                        .file(file)
                        .param("user", "uploadTest")
                        .param("visibility", "PUBLIC")
                        .param("tags", "myTag1", "myTag2"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetPublicFile() throws Exception {
        // Perform a GET request
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/storage/v1/files/public"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(5, files.stream().toList().size());
    }

    @Test
    public void testGetPublicFileWithFilterByTag() throws Exception {
        // Perform a GET request
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/storage/v1/files/public?filterByTag=myTag0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.stream().toList().size());
    }

    @Test
    public void testGetPublicFileSortedBy() throws Exception {
        // Perform a GET request
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/storage/v1/files/public?sortedBy=tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(5, files.stream().toList().size());
    }

    @Test
    public void testGetUserFiles() throws Exception {
        // Perform a GET request
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/storage/v1/files/user?user=test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(5, files.stream().toList().size());
        Assertions.assertEquals(2,
                files.stream()
                        .filter(file -> FileVisibility.PRIVATE.equals(file.getVisibility()))
                        .toList()
                        .size());
        Assertions.assertEquals(3,
                files.stream()
                        .filter(file -> FileVisibility.PUBLIC.equals(file.getVisibility()))
                        .toList()
                        .size());
    }

    @Test
    public void testGetUserFilesWithFilterByTag() throws Exception {
        // Perform a GET request
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                "/api/storage/v1/files/user?user=test&filterByTag=myTag0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.stream().toList().size());
    }

    @Test
    public void testGetUserFilesSortedBy() throws Exception {
        // Perform a GET request
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                "/api/storage/v1/files/user?user=test&sortedBy=tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(5, files.stream().toList().size());
    }

    @Test
    public void testRenameFile() throws Exception {
        // Perform a PUT request
        String user = "test";
        String filename = "file0.txt";
        String newFilename = "renamedFile.txt";

        // Perform a PUT request to /api/files/rename with parameters
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/storage/v1/files/rename")
                        .param("user", user)
                        .param("filename", filename)
                        .param("newFilename", newFilename)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        // Assert the response content
        resultActions.andExpect(MockMvcResultMatchers.content().string("File renamed successfully"));


         resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                "/api/storage/v1/files/user?user=test&filterByTag=mytag0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertTrue(files.stream().anyMatch(file -> file.getFileName().equals("renamedFile.txt")));
    }

    @Test
    public void testDeleteFilesByUserAndName() throws Exception {
        String user = "test";
        String filename = "file0.txt";

        // Perform a DELETE request
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/storage/v1/files/delete")
                        .param("user", user)
                        .param("filename", filename)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        // Assert the response content
        resultActions.andExpect(MockMvcResultMatchers.content().string("File deleted successfully"));

        resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                "/api/storage/v1/files/user?user=test&filterByTag=mytag0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Extract the response content as a string
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();

        // Deserialize the response
        Page<FileMetadataDTO> files = objectMapper.readValue(responseContent,
                new TypeReference<CustomPageImpl<FileMetadataDTO>>() {
                });

        Assertions.assertNotNull(files);
        Assertions.assertEquals(0, files.stream().toList().size());

    }
}

