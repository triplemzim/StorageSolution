package com.muhim.storage.utils;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to provide common utility methods for storage/file operations
 *
 * @author muhim
 */
public class FileUtils {

    /**
     * Get base URL dynamically and create download link
     *
     * @param objectId reference from MongoDB
     * @param baseUrl  base URL from request
     * @return download link
     */
    public static String createDownloadLink(ObjectId objectId, String baseUrl) {
        return String.format(baseUrl +
                        "/api/storage/v1/files/%s/download",
                objectId.toHexString());
    }

    /**
     * Generate a rolling hash using MD5 algorithm
     * Uses 1024 byte chunk size for each hash
     *
     * @param inputStream to consume and generate the rolling hash
     * @return md5 hash string
     * @throws IOException can throw IOException on issues accessing the input stream
     * @throws NoSuchAlgorithmException can throw while creating message digest object with `MD5` algorithm
     */
    public static String generateFileRollingHash(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (inputStream) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
