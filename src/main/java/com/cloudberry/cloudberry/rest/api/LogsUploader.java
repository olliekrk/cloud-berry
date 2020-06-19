package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.RawLogsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("logs")
@RequiredArgsConstructor
public class LogsUploader {

    private final RawLogsHandler rawLogsHandler;

    @PostMapping("/upload")
    public boolean upload(@RequestParam("file") MultipartFile file) throws IOException {
        String rawLogs = new String(file.getBytes());
        String a;
        return rawLogsHandler.saveLogsToDatabase(rawLogs);
    }
}
