package com.muhim.storage.repository;

import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.model.FileMetadata;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * Wrapper interface to enhance operation and query to MongoDB
 */
public interface FileMetaDataRepository extends MongoRepository<FileMetadata, String> {
    boolean existsByFilenameAndUser(String filename, String user);

    boolean existsByFileId(ObjectId fileId);

    Page<FileMetadata> findByVisibility(FileVisibility visibility, Pageable pageable);

    Page<FileMetadata> findByVisibilityAndTagsIgnoreCase(FileVisibility visibility,
                                                         String tag,
                                                         Pageable pageable);

    Page<FileMetadata> findByUser(String user, Pageable pageable);

    Page<FileMetadata> findByUserAndTagsIgnoreCase(String user,
                                                   String tag,
                                                   Pageable pageable);

    FileMetadata findByFileId(ObjectId fileId);

    FileMetadata findByFilenameAndUser(String filename, String user);
}
