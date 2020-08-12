package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.service.LogsImporterService;
import com.cloudberry.cloudberry.model.statistics.DataPoint;
import com.cloudberry.cloudberry.rest.dto.DataFilters;
import com.cloudberry.cloudberry.rest.exceptions.FileImportException;
import com.cloudberry.cloudberry.rest.exceptions.RestException;
import com.cloudberry.cloudberry.service.api.RawDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/raw")
@RequiredArgsConstructor
@Slf4j
public class RawDataRest {

    private final RawDataService rawDataService;
    private final LogsImporterService logsImporterService;

    @PostMapping("/save")
    public void saveData(@RequestParam(required = false) String measurementName,
                         @RequestParam(required = false) String bucketName,
                         @RequestBody List<DataPoint> dataPoints) {
        rawDataService.saveData(measurementName, bucketName, dataPoints);
    }

    @PostMapping(value = "/find", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findData(@RequestParam(required = false) String measurementName,
                                              @RequestParam(required = false) String bucketName,
                                              @RequestBody DataFilters filters) {
        return rawDataService.findData(measurementName, bucketName, filters);
    }

    @DeleteMapping
    public void deleteComputationLogs(@RequestParam(required = false) String measurementName,
                                      @RequestParam(required = false) String bucketName) {
        rawDataService.deleteComputationLogs(measurementName, bucketName);
    }

    @PostMapping(value = "/file/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean uploadAgeFile(@PathVariable String experimentName,
                                 @RequestPart MultipartFile file,
                                 @RequestPart Map<String, String> headersKeys,
                                 @RequestPart Map<String, String> headersMeasurements) throws RestException {
        Path tmpPath = null;
        var ok = true;
        var importDetails = new ImportDetails(headersKeys, headersMeasurements);
        try {
            tmpPath = createTemporaryFile(file.getOriginalFilename());
            file.transferTo(tmpPath);
            logsImporterService.importExperimentFile(tmpPath.toFile(), importDetails, experimentName);
        } catch (IOException e) {
            throw new FileImportException(e);
        } finally {
            if (tmpPath != null) {
                ok = tmpPath.toFile().delete();
            }
        }

        return ok;
    }

    private static Path createTemporaryFile(String originalFileName) throws IOException {
        var prefix = "log_" + Instant.now().toEpochMilli() + "_";
        return Files.createTempFile(prefix, originalFileName);
    }
}
