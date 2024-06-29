package com.muhim.storage.utils;

import org.bson.types.ObjectId;

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
}
