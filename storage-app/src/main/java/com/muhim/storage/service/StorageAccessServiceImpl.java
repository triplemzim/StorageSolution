package com.muhim.storage.service;

import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.model.FileMetadata;
import com.muhim.storage.repository.FileMetaDataRepository;
import com.muhim.storage.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * StorageAccessService implementation
 *
 * @author muhim
 */
@Service
public class StorageAccessServiceImpl implements StorageAccessService {

    private static final int DEFAULT_PAGE_SIZE = 5;

    @Autowired
    FileMetaDataRepository fileMetaDataRepository;

    @Override
    public Page<FileMetadataDTO> getPublicFiles(int page, String sortBy, String tag, String baseUrl) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE,
                Sort.by(Sort.Direction.ASC, mapSortByField(sortBy)));

        Page<FileMetadata> fileMetadataPage;

        if (tag == null || tag.isEmpty()) {
            fileMetadataPage = fileMetaDataRepository
                    .findByVisibility(FileVisibility.PUBLIC, pageable);
        } else {
            fileMetadataPage = fileMetaDataRepository.findByVisibilityAndTagsIgnoreCase(
                    FileVisibility.PUBLIC,
                    tag,
                    pageable);

        }

        List<FileMetadataDTO> fileMetadataDTOList = fileMetadataPage.getContent().stream()
                .map(file -> convertToFileMetadataDTO(file, baseUrl))
                .toList();

        return new PageImpl<>(fileMetadataDTOList, pageable, fileMetadataPage.getTotalElements());
    }


    @Override
    public Page<FileMetadataDTO> getUserFiles(String user,
                                              int page,
                                              String sortBy,
                                              String tag,
                                              String baseUrl) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE,
                Sort.by(Sort.Direction.ASC, mapSortByField(sortBy)));

        Page<FileMetadata> fileMetadataPage;

        if (tag == null || tag.isEmpty()) {
            fileMetadataPage = fileMetaDataRepository
                    .findByUser(user, pageable);
        } else {
            fileMetadataPage = fileMetaDataRepository.findByUserAndTagsIgnoreCase(
                    user,
                    tag,
                    pageable);

        }

        List<FileMetadataDTO> fileMetadataDTOList = fileMetadataPage.getContent().stream()
                .map(file -> convertToFileMetadataDTO(file, baseUrl))
                .toList();

        return new PageImpl<>(fileMetadataDTOList, pageable, fileMetadataPage.getTotalElements());
    }

    private FileMetadataDTO convertToFileMetadataDTO(FileMetadata fileMetadata, String baseUrl) {
        return FileMetadataDTO.builder()
                .visibility(fileMetadata.getVisibility())
                .fileName(fileMetadata.getFilename())
                .uploadDate(fileMetadata.getUploadDate())
                .downloadLink(FileUtils.createDownloadLink(fileMetadata.getFileId(), baseUrl))
                .tags(fileMetadata.getTags())
                .build();
    }

    private String mapSortByField(String sortBy) {
        Map<String, String> sortByFieldMap = Map.of(
                "filename", "filename",
                "uploaddate", "uploadDate",
                "filesize", "fileSize",
                "contenttype", "contentType",
                "tag", "tag"
        );
        return sortByFieldMap.getOrDefault(sortBy.toLowerCase(), "filename");
    }
}
