package com.muhim.storage.service;

import com.muhim.storage.dto.FileMetadataDTO;
import org.springframework.data.domain.Page;

/**
 * Interface to list and access files
 *
 * @author muhim
 */
public interface StorageAccessService {
    /**
     * Validates and find list of public files with given params
     *
     * @param page helps to find the page for paginated response
     * @param sortBy sort by pre-defined parameter
     * @param tag is used to filter the result
     * @param baseUrl baseUrl to dynamically generate download link
     * @return paginated FileMetaData objects
     */
    Page<FileMetadataDTO> getPublicFiles(int page, String sortBy, String tag, String baseUrl);

    /**
     * Validates and find list of user specific files with given params
     *
     * @param user the user to get the files of
     * @param page helps to find the page for paginated response
     * @param sortBy sort by pre-defined parameter
     * @param tag is used to filter the result
     * @param baseUrl baseUrl to dynamically generate download link
     * @return paginated FileMetaData objects
     */
    Page<FileMetadataDTO> getUserFiles(String user,
                                       int page,
                                       String sortBy,
                                       String tag,
                                       String baseUrl);
}
