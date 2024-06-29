package com.muhim.storage.dto;

import com.muhim.storage.enums.FileVisibility;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class FileMetadataDTO {
    private String fileName;
    private FileVisibility visibility;
    private List<String> tags;
    private Date uploadDate;
    private String downloadLink;
}
