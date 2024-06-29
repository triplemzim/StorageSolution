package com.muhim.storage.utils;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


class FileUtilsTest {

    @Test
    void createDownloadLink() {
        String hexId = "507f1f77bcf86cd799439011";
        ObjectId objectId = new ObjectId(hexId);
        String baseUrl = "localhost";
        String downloadLink = FileUtils.createDownloadLink(objectId, baseUrl);

        Assertions.assertTrue(downloadLink.contains(baseUrl));
        Assertions.assertTrue(downloadLink.contains(hexId));
    }

    @Test
    void generateFileRollingHash() throws IOException, NoSuchAlgorithmException {
        String dummyString = "dummyString";
        String rollingHash = FileUtils.generateFileRollingHash(new ByteArrayInputStream(dummyString.getBytes()));

        Assertions.assertNotNull(rollingHash);
        Assertions.assertFalse(rollingHash.isEmpty());
    }
}