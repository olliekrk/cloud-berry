package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataEvictor;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.rest.dto.ComputationLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataService {
    private final PointBuilder pointBuilder;
    private final InfluxDataWriter influxDataWriter;
    private final InfluxDataEvictor influxDataEvictor;

    public void saveComputationLogs(String measurementName,
                                    @Nullable String bucketName,
                                    List<ComputationLogDto> computationLogs) {
        var computationPoints = computationLogs.stream()
                .map(log -> pointBuilder.buildPoint(measurementName, log.getTime(), log.getFields(), log.getTags()))
                .collect(Collectors.toList());

        log.info("Writing " + computationLogs.size() + " computation logs to Influx DB");
        influxDataWriter.writePoints(bucketName, computationPoints);
    }

    public void deleteComputationLogs(String measurementName,
                                      @Nullable String bucketName) {
        influxDataEvictor.deleteComputationLogs(bucketName, measurementName);
    }
}
