package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.service.LogsImporterService;
import com.cloudberry.cloudberry.model.statistics.DataPoint;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.dto.DataFilters;
import com.cloudberry.cloudberry.rest.exceptions.FileImportException;
import com.cloudberry.cloudberry.service.api.RawDataService;
import com.cloudberry.cloudberry.util.FileSystemUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
    private final LogsImporterService logsImporterService;

    @PostMapping("/save")
    public void saveData(@RequestParam(required = false) String bucketName,
                         @RequestParam(required = false) String measurementName,
                         @RequestBody List<DataPoint> dataPoints) {
        rawDataService.saveData(bucketName, measurementName, dataPoints);
    }

    @PostMapping(value = "/find", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DataSeries findData(@RequestParam(required = false) String bucketName,
                               @RequestParam(required = false) String measurementName,
                               @RequestBody DataFilters filters) {
        return rawDataService.findData(bucketName, measurementName, filters);
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteData(@RequestParam(required = false) String bucketName,
                           @RequestParam(required = false) String measurementName,
                           @RequestBody DataFilters filters) {
        rawDataService.deleteData(bucketName, measurementName, filters);
    }

    @PostMapping(value = "/file/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectId uploadAgeFile(@PathVariable String experimentName,
                                  @RequestPart MultipartFile file,
                                  @RequestPart(required = false) Map<String, String> headersKeys,
                                  @RequestPart(required = false) Map<String, String> headersMeasurements) {
        var importDetails = new ImportDetails(headersKeys, headersMeasurements);
        try {
            return FileSystemUtils.<ObjectId>withTemporaryFile(
                    file,
                    temporaryFilePath -> {
                        try {
                            return logsImporterService.importAgeFile(temporaryFilePath.toFile(), importDetails, experimentName);
                        } catch (IOException e) {
                            throw new FileImportException(e);
                        }
                    }
            );
        } catch (IOException e) {
            throw new FileImportException(e);
        }
    }
}
