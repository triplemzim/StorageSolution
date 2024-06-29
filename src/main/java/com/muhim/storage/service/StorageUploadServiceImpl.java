package com.muhim.storage.service;

import com.muhim.storage.clients.GridFsClient;
import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import com.muhim.storage.utils.FileUtils;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@Service
public class StorageUploadServiceImpl implements StorageUploadService {

    private static final int MAX_TAG_LIMIT = 5;

    private final FileMetaDataRepository fileMetaDataRepository;

    private final GridFsClient gridFsClient;

    private final Tika tika;

    @Autowired
    public StorageUploadServiceImpl(GridFsClient gridFsClient, FileMetaDataRepository fileMetaDataRepository) {
        this.tika = new Tika();
        this.gridFsClient = gridFsClient;
        this.fileMetaDataRepository = fileMetaDataRepository;
    }


    @Override
    public FileMetadataDTO saveFile(String user,
                                    MultipartFile file,
                                    FileVisibility visibility,
                                    List<String> tags,
                                    String baseUrl) throws IOException, NoSuchAlgorithmException {

        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = tika.detect(file.getInputStream());
        }
        validateTags(tags);
        validateContentType(contentType);

        ObjectId gridFsObjectId = saveFile(file.getOriginalFilename(), file, contentType);

        String fileRollingHash = FileUtils.generateFileRollingHash(gridFsClient.openDownloadStream(gridFsObjectId));
        validateFile(user, file, gridFsObjectId, fileRollingHash);


        FileMetadata fileMetadata = FileMetadata.builder()
                .user(user)
                .filename(file.getOriginalFilename())
                .fileId(gridFsObjectId)
                .visibility(visibility)
                .tags(tags.stream().sorted().toList())
                .fileSize(file.getSize())
                .contentType(contentType)
                .uploadDate(new Date())
                .fileRollingHash(fileRollingHash)
                .build();


        FileMetadata savedFileMetadata = fileMetaDataRepository.save(fileMetadata);
        return convertToFileMetadataDTO(savedFileMetadata, baseUrl);
    }

    private FileMetadataDTO convertToFileMetadataDTO(FileMetadata fileMetadata, String baseUrl) {
        return FileMetadataDTO.builder()
                .fileName(fileMetadata.getFilename())
                .uploadDate(fileMetadata.getUploadDate())
                .visibility(fileMetadata.getVisibility())
                .downloadLink(FileUtils.createDownloadLink(fileMetadata.getFileId(), baseUrl))
                .tags(fileMetadata.getTags())
                .build();
    }


    private ObjectId saveFile(String fileName, MultipartFile file, String contentType) throws IOException {
        return gridFsClient.store(file.getInputStream(), fileName, contentType);
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            throw new IllegalStateException("Couldn't determine file contentType");
        }
    }

    private void validateFile(String user, MultipartFile file, ObjectId gridFsObjectId, String fileRollingHash) {
        if (fileMetaDataRepository.existsByUserAndFilename(user, file.getOriginalFilename()) ||
                fileMetaDataRepository.existsByUserAndFileRollingHash(user, fileRollingHash)) {
            gridFsClient.delete(gridFsObjectId);
            throw new IllegalArgumentException("File already exists");
        }
    }

    private void validateTags(List<String> tags) {
        if (tags.size() > MAX_TAG_LIMIT) {
            throw new IllegalArgumentException("Tags limit exceeded! Max " +
                    MAX_TAG_LIMIT +
                    " tags allowed.");
        }
    }

}
