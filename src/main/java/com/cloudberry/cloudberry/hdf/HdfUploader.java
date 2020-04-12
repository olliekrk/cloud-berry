package com.cloudberry.cloudberry.hdf;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class HdfUploader {

    final HdfRepository hdfRepository;

    public HdfUploader(HdfRepository hdfRepository) {
        this.hdfRepository = hdfRepository;
    }

    public HdfFile uploadFile(MultipartFile file) throws IOException {
        var hdf = new HdfFile(file.getName(), new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        return hdfRepository.insert(hdf).block();
    }
}
