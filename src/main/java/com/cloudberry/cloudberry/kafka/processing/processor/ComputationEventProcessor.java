package com.cloudberry.cloudberry.kafka.processing.processor;

import com.cloudberry.cloudberry.config.async.AsyncExecutors;
import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.processing.EventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComputationEventProcessor implements EventProcessor<ComputationEvent> {

    private final PointBuilder pointBuilder;
    private final InfluxDataWriter influxDataWriter;

    @Override
    @Async(AsyncExecutors.influxProcessorsExecutor)
    public void process(ComputationEvent event) {
        process(event, null);
    }

    @Async(AsyncExecutors.influxProcessorsExecutor)
    public void process(ComputationEvent event, @Nullable String bucketName) {
        var point = pointBuilder.buildPoint(
                event.getMeasurementName(),
                event.getTime(),
                event.getFields(),
                event.getTags()
        );

        influxDataWriter.writePoint(bucketName, point);
    }

}
