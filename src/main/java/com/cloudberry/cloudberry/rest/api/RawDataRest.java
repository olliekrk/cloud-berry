package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.rest.dto.ComputationLogDto;
import com.cloudberry.cloudberry.rest.dto.LogFilters;
import com.cloudberry.cloudberry.service.RawLogsHandler;
import com.cloudberry.cloudberry.service.api.RawDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/raw")
@RequiredArgsConstructor
@Slf4j
public class RawDataRest {

    private final RawDataService rawDataService;
    private final RawLogsHandler rawLogsHandler;

    @PostMapping("/save/{measurementName}")
    public void saveComputationLogs(@PathVariable String measurementName,
                                    @RequestParam(required = false) String bucketName,
                                    @RequestBody List<ComputationLogDto> computationLogs) {
        rawDataService.saveComputationLogs(measurementName, bucketName, computationLogs);
    }

    @PostMapping(value = "/find/{measurementName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findComputationLogs(@PathVariable String measurementName,
                                                         @RequestParam(required = false) String bucketName,
                                                         @RequestBody LogFilters filters) {
        return rawDataService.findComputationLogs(measurementName, bucketName, filters);
    }

    @DeleteMapping("/{measurementName}")
    public void deleteComputationLogs(@PathVariable String measurementName,
                                      @RequestParam(required = false) String bucketName) {
        rawDataService.deleteComputationLogs(measurementName, bucketName);
    }

    @PostMapping("/file")
    public boolean saveFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        String rawLogs = new String(file.getBytes());
        return rawLogsHandler.saveLogsToDatabase(rawLogs);
    }

}
