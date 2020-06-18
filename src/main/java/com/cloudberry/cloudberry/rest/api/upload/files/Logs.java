package com.cloudberry.cloudberry.rest.api.upload.files;

import com.cloudberry.cloudberry.service.LogsParser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("logs")
@RequiredArgsConstructor
public class Logs {

    private final List<String> files;
    private final LogsParser logsParser;

    @GetMapping("/all")
    public List<String> getAllFileNames() {
        return files;
    }

    @PostMapping("/upload")
    public boolean upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        files.add(fileName);
        String content = new String(file.getBytes());
        return logsParser.saveLogsToDatabase(content);
    }
}
