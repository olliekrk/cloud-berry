package com.cloudberry.cloudberry.hdf;

import com.cloudberry.cloudberry.EmbeddedMongoTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ComponentScan("com.cloudberry.cloudberry.hdf")
@EmbeddedMongoTests
class HdfUploaderTest {
    @Autowired
    private HdfUploader hdfUploader;

    @Test
    @DisplayName("it should upload file with given file name")
    public void uploadFileTest() throws IOException {
        var file = mockFile();
        var uploadedFile = hdfUploader.uploadFile(file);

        assertEquals(file.getName(), uploadedFile.fileName);
        assertArrayEquals(file.getBytes(), uploadedFile.file.getData());
    }

    private MultipartFile mockFile() {
        return new MockMultipartFile(
                "test.hdf",
                "test.hdf",
                "application/x-hdf",
                "Hello there!".getBytes()
        );
    }
}
