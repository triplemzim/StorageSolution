package com.muhim.storage.repository;

import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.model.FileMetadata;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * Wrapper interface to enhance operations and provide additional queries to MongoDB
 *
 * @author muhim
 */
public interface FileMetaDataRepository extends MongoRepository<FileMetadata, String> {
    boolean existsByUserAndFilename(String user, String filename);

    boolean existsByFileId(ObjectId fileId);

    boolean existsByUserAndFileRollingHash(String user, String fileRollingHash);

    Page<FileMetadata> findByVisibility(FileVisibility visibility, Pageable pageable);

    Page<FileMetadata> findByVisibilityAndTagsIgnoreCase(FileVisibility visibility,
                                                         String tag,
                                                         Pageable pageable);

    Page<FileMetadata> findByUser(String user, Pageable pageable);

    Page<FileMetadata> findByUserAndTagsIgnoreCase(String user,
                                                   String tag,
                                                   Pageable pageable);

    FileMetadata findByFileId(ObjectId fileId);

    FileMetadata findByUserAndFilename(String user, String filename);
}
