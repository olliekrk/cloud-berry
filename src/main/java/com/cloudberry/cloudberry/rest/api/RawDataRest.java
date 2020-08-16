package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.model.statistics.DataPoint;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.rest.dto.DataFilters;
import com.cloudberry.cloudberry.rest.exceptions.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.InvalidEvaluationIdException;
import com.cloudberry.cloudberry.rest.exceptions.RestException;
import com.cloudberry.cloudberry.service.api.RawDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/raw")
@RequiredArgsConstructor
@Slf4j
public class RawDataRest {

    private final RawDataService rawDataService;

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

    @PostMapping(value = "/ageFile/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectId uploadAgeFile(@PathVariable String experimentName,
                                  @RequestPart MultipartFile file,
                                  @RequestPart(required = false) Map<String, String> headersKeys,
                                  @RequestPart(required = false) Map<String, String> headersMeasurements) {
        var uploadDetails = new AgeUploadDetails(headersKeys, headersMeasurements);
        return rawDataService.uploadAgeFile(file, experimentName, uploadDetails);
    }

    @PostMapping(value = "/csvFile/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectId uploadCsvFile(@PathVariable String experimentName,
                                  @RequestPart MultipartFile file,
                                  @RequestPart(required = false) List<String> tags,
                                  @RequestParam String configurationId,
                                  @RequestParam(required = false) String evaluationId,
                                  @RequestParam(required = false) String measurementName) throws RestException {
        if (!ObjectId.isValid(configurationId))
            throw new InvalidConfigurationIdException(List.of(configurationId));

        if (evaluationId != null && !ObjectId.isValid(evaluationId))
            throw new InvalidEvaluationIdException(List.of(evaluationId));

        var uploadDetails = new CsvUploadDetails(
                tags == null ? List.of() : tags,
                new ObjectId(configurationId),
                evaluationId == null ? new ObjectId() : new ObjectId(evaluationId),
                measurementName
        );
        return rawDataService.uploadCsvFile(file, experimentName, uploadDetails);
    }

}
