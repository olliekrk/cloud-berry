package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.DataPoint;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.db.influx.model.DataFilters;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.rest.exceptions.RestException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.BucketNameResolver;
import com.cloudberry.cloudberry.service.api.RawDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
    private final BucketNameResolver bucketNameResolver;
    private final RawDataService rawDataService;

    @PostMapping("/save")
    public void saveData(@RequestParam(required = false) String bucketName,
                         @RequestParam(required = false) String measurementName,
                         @RequestBody List<DataPoint> dataPoints) {
        rawDataService.saveData(
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
                dataPoints);
    }

    @PostMapping(value = "/find", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DataSeries findData(@RequestParam(required = false) String bucketName,
                               @RequestParam(required = false) String measurementName,
                               @RequestBody DataFilters filters) {
        return rawDataService.findData(
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
                filters);
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteData(@RequestParam(required = false) String bucketName,
                           @RequestParam(required = false) String measurementName,
                           @RequestBody DataFilters filters) {
        rawDataService.deleteData(
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
                filters);
    }

    @PostMapping(value = "/ageFile/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectId uploadAgeFile(@PathVariable String experimentName,
                                  @RequestPart MultipartFile file,
                                  @RequestPart(required = false) Map<String, String> headersKeys,
                                  @RequestPart(required = false) Map<String, String> headersMeasurements,
                                  @RequestParam(required = false) String configurationName) {
        var uploadDetails = new AgeUploadDetails(headersKeys, headersMeasurements, configurationName);
        return rawDataService.uploadAgeFile(file, experimentName, uploadDetails);
    }

    @PostMapping(value = "/csvFile/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectId uploadCsvFile(@PathVariable String experimentName,
                                  @RequestPart MultipartFile file,
                                  @RequestPart(name = "tags", required = false) List<String> tagsParam,
                                  @RequestPart(name = "headers", required = false) List<String> headersParam,
                                  @RequestParam String configurationIdHex,
                                  @RequestParam(required = false) String computationId,
                                  @RequestParam(required = false) String measurementName,
                                  @RequestParam(defaultValue = "false") String hasHeaders) throws RestException {

        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        if (computationId != null && !ObjectId.isValid(computationId))
            throw new InvalidComputationIdException(List.of(computationId));

        var firstRecordAsHeaders = hasHeaders.equals("true");
        var uploadDetails = new CsvUploadDetails(
                tagsParam == null ? List.of() : tagsParam,
                configurationId,
                computationId == null ? new ObjectId() : new ObjectId(computationId),
                measurementName,
                firstRecordAsHeaders || headersParam == null ? null : headersParam
        );
        return rawDataService.uploadCsvFile(file, experimentName, uploadDetails);
    }

}
