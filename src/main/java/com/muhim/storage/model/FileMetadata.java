package com.muhim.storage.model;

import com.muhim.storage.enums.FileVisibility;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
@Builder
public class FileMetadata {
    @Id
    private String id;
    private ObjectId fileId;
    private String user;
    private String filename;
    private FileVisibility visibility;
    private List<String> tags;
    private Date uploadDate;
    private String contentType;
    private long fileSize;
    private String fileRollingHash;
}
