package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.basic.DataPoint;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.parsing.model.FieldTypes;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.rest.exceptions.RestException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.RawDataService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/raw")
@RequiredArgsConstructor
@Slf4j
public class RawDataRest {
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;
    private final RawDataService rawDataService;

    @PostMapping("/save")
    public void saveData(
            @RequestParam(required = false) String bucketName,
            @RequestParam(required = false) String measurementName,
            @RequestBody List<DataPoint> dataPoints
    ) {
        rawDataService.saveData(
                influxQueryFieldsResolver.get(measurementName, bucketName),
                dataPoints
        );
    }

    @PostMapping(value = "/find", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DataSeries findData(
            @RequestParam(required = false) String bucketName,
            @RequestParam(required = false) String measurementName,
            @RequestBody DataFilters filters
    ) {
        return rawDataService.findData(
                influxQueryFieldsResolver.get(measurementName, bucketName),
                filters
        );
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteData(
            @RequestParam(required = false) String bucketName,
            @RequestParam(required = false) String measurementName,
            @RequestBody DataFilters filters
    ) {
        rawDataService.deleteData(
                influxQueryFieldsResolver.get(measurementName, bucketName),
                filters
        );
    }

    @PostMapping(value = "/ageFile/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExperimentComputation uploadAgeFile(
            @PathVariable String experimentName,
            @RequestPart MultipartFile file,
            @RequestPart Map<String, String> headersKeys,
            @RequestPart(required = false) Map<String, String> headersMeasurements,
            @RequestPart(name = "raws", required = false) List<String> rawsParam,
            @RequestParam(required = false) String configurationName
    ) {
        var fieldTypes = rawsParam == null ? FieldTypes.empty() : FieldTypes.allStrings(rawsParam);
        var uploadDetails = new AgeUploadDetails(
                headersKeys,
                headersMeasurements,
                configurationName,
                fieldTypes
                );
        return rawDataService.uploadAgeFile(file, experimentName, uploadDetails);
    }

    @PostMapping(value = "/csvFile/{experimentName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExperimentComputation uploadCsvFile(
            @PathVariable String experimentName,
            @RequestPart MultipartFile file,
            @RequestPart(name = "tags", required = false) List<String> tagsParam,
            @RequestPart(name = "headers", required = false) List<String> headersParam,
            @RequestPart(name = "raws", required = false) List<String> rawsParam,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String computationId,
            @RequestParam(required = false) String measurementName,
            @RequestParam(defaultValue = "false") String hasHeaders
    ) throws RestException {

        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        if (computationId != null && !ObjectId.isValid(computationId)) {
            throw new InvalidComputationIdException(List.of(computationId));
        }

        var fieldTypes = rawsParam == null ? FieldTypes.empty() : FieldTypes.allStrings(rawsParam);
        var firstRecordAsHeaders = hasHeaders.equals("true");
        var uploadDetails = new CsvUploadDetails(
                tagsParam == null ? List.of() : tagsParam,
                configurationId,
                Optional.ofNullable(computationId).filter(ObjectId::isValid).map(ObjectId::new).orElse(null),
                measurementName,
                firstRecordAsHeaders || headersParam == null ? null : headersParam,
                fieldTypes
        );
        return rawDataService.uploadCsvFile(file, experimentName, uploadDetails);
    }

}
