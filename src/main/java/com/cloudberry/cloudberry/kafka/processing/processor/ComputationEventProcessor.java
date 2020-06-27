package com.cloudberry.cloudberry.kafka.processing.processor;

import com.cloudberry.cloudberry.db.influx.point.PointBuilder;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.processing.EventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComputationEventProcessor implements EventProcessor<ComputationEvent> {

    private final PointBuilder pointBuilder;
    private final InfluxDataWriter influxDataWriter;

    @Override
    @Async
    public void process(ComputationEvent event) {
        var point = pointBuilder.buildPoint(
                event.getMeasurementName(),
                event.getTime(),
                event.getFields(),
                event.getTags()
        );

        influxDataWriter.writePoint(null, point);
    }

}
