package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.service.LogsImporterService;
import com.cloudberry.cloudberry.rest.dto.ComputationLogDto;
import com.cloudberry.cloudberry.rest.dto.LogFilters;
import com.cloudberry.cloudberry.rest.exceptions.FileCouldNotBeImportedException;
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

    @PostMapping(value = "/file/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean uploadDataFile(@PathVariable String experimentName,
                                  @RequestPart MultipartFile file,
                                  @RequestPart Map<String, String> headersKeys,
                                  @RequestPart Map<String, String> headersMeasurements) throws RestException {
        Path tmpPath = null;
        var ok = false;
        var importDetails = new ImportDetails(headersKeys, headersMeasurements);
        try {
            tmpPath = Files.createTempFile("log_" + Instant.now().toString() + "_", file.getOriginalFilename());
            file.transferTo(tmpPath);
            ok = logsImporterService.importExperimentFile(tmpPath.toFile(), importDetails, experimentName);
        } catch (IOException e) {
            throw new FileCouldNotBeImportedException(e);
        } finally {
            if (tmpPath != null) {
                ok = tmpPath.toFile().delete();
            }
        }

        return ok;
    }

}
