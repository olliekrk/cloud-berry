package com.cloudberry.cloudberry.hdf;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class HdfUploader {

    final HdfRepository hdfRepository;

    public HdfUploader(HdfRepository hdfRepository) {
        this.hdfRepository = hdfRepository;
    }

    public ObjectId uploadFile(String fileName, MultipartFile file) throws IOException {
        var hdf = new HdfFile(fileName, new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        return hdfRepository.insert(hdf).map(HdfFile::getId).block();
    }
}
