package com.muhim.storage.controller;

import com.muhim.storage.dto.FileMetadataDTO;
import com.muhim.storage.enums.FileVisibility;
import com.muhim.storage.service.StorageAccessService;
import com.muhim.storage.service.StorageDownloadService;
import com.muhim.storage.service.StorageModificationService;
import com.muhim.storage.service.StorageUploadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.InputStream;
import java.util.List;

/**
 * Controller for storage solution
 * Provides REST API endpoint for file management
 *
 * @author muhim
 */
@RestController
@RequestMapping("api/storage/v1")
public class StorageController {

    private static final String FILENAME = "filename;";

    private final StorageUploadService storageUploadService;
    private final StorageDownloadService storageDownloadService;
    private final StorageModificationService storageModificationService;
    private final StorageAccessService storageAccessService;

    @Autowired
    public StorageController(StorageUploadService storageUploadService,
                             StorageDownloadService storageDownloadService,
                             StorageModificationService storageModificationService,
                             StorageAccessService storageAccessService) {
        this.storageUploadService = storageUploadService;
        this.storageDownloadService = storageDownloadService;
        this.storageModificationService = storageModificationService;
        this.storageAccessService = storageAccessService;
    }


    @PostMapping("/files/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("user") String user,
            @RequestParam("file") MultipartFile file,
            @RequestParam("visibility") FileVisibility visibility,
            @RequestParam("tags") List<String> tags,
            HttpServletRequest request) {
        try {
            return ResponseEntity.ok()
                    .body(storageUploadService.saveFile(user, file, visibility, tags, getBaseUrl(request)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) {
        try {
            InputStream inputStream = storageDownloadService.downloadFileStream(id);
            String originalFilename = storageDownloadService.getOriginalFilename(id);

            // Set Content-Disposition header to prompt download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", originalFilename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/files/delete")
    public ResponseEntity<String> deleteFilesByUserAndName(
            @RequestParam("user") String user,
            @RequestParam("filename") String filename) {

        try {
            storageModificationService.deleteFilesByUserAndName(user, filename);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete files: " + e.getMessage());
        }
    }

    @PutMapping("/files/rename")
    public ResponseEntity<String> renameFile(
            @RequestParam("user") String user,
            @RequestParam("filename") String filename,
            @RequestParam("newFilename") String newFilename) {

        try {
            storageModificationService.renameFile(user, filename, newFilename);
            return ResponseEntity.ok("File renamed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to rename file: " + e.getMessage());
        }
    }

    @GetMapping("/files/public")
    public ResponseEntity<Page<FileMetadataDTO>> getPublicFiles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = FILENAME) String sortBy,
            @RequestParam(value = "filterByTag", defaultValue = "") String tag,
            HttpServletRequest request) {
        Page<FileMetadataDTO> files = storageAccessService.getPublicFiles(page, sortBy, tag, getBaseUrl(request));
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/user")
    public ResponseEntity<Page<FileMetadataDTO>> getUserFiles(
            @RequestParam("user") String user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = FILENAME) String sortBy,
            @RequestParam(value = "filterByTag", defaultValue = "") String tag,
            HttpServletRequest request) {

        Page<FileMetadataDTO> userFiles = storageAccessService.getUserFiles(user,
                page,
                sortBy,
                tag,
                getBaseUrl(request));
        return ResponseEntity.ok(userFiles);
    }

    private String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }
}
