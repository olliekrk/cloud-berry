package com.cloudberry.cloudberry.hdf;

import lombok.Data;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Suitable for small files only (size < 16MB).
 * Consider using GridFS if support for larger files is necessary.
 */
@Data
@Document(collection = "hdf")
public class HdfFile {
    @Id
    private ObjectId id;
    public final String fileName;
    public final Binary file;

    public HdfFile(String fileName, Binary file) {
        this.fileName = fileName;
        this.file = file;
    }
}
