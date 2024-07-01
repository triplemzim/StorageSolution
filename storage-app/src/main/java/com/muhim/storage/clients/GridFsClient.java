package com.muhim.storage.clients;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * GridFsClient to mediate the communication between service and database
 * It uses GrdFsTemplate and GridFsBucket to enable database operations for files
 *
 * @author muhim
 */
@Component
public class GridFsClient {

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;

    @Autowired
    public GridFsClient(MongoDatabaseFactory mongoDbFactory, GridFsTemplate gridFsTemplate) {
        this.gridFSBucket = GridFSBuckets.create(mongoDbFactory.getMongoDatabase());
        this.gridFsTemplate = gridFsTemplate;
    }

    public ObjectId store(InputStream inputStream, String fileName, String contentType) {
        return gridFsTemplate.store(inputStream, fileName, contentType);
    }


    public void delete(ObjectId gridFsObjectId) {
        gridFSBucket.delete(gridFsObjectId);
    }

    public InputStream openDownloadStream(ObjectId objectId) {
        return gridFSBucket.openDownloadStream(objectId);
    }
}
