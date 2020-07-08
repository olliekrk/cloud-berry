package com.cloudberry.cloudberry.kafka.processing.processor;

import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.db.influx.util.converter.LogsEventToPointConverter;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import com.cloudberry.cloudberry.kafka.processing.EventProcessor;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogsEventProcessor implements EventProcessor<Event> {
    @Value("${influx.buckets.default-logs}")
    private String evaluationLogsBucketName;
    private final InfluxDataWriter influxDataWriter;
    private final LogsEventToPointConverter logsEventToPointConverter;

    @Async
    public void process(Event event) {
        Point point = null;
        if (event instanceof WorkplaceEvent) {
            point = logsEventToPointConverter.workplaceEventConverter().convert((WorkplaceEvent) event);
        } else if (event instanceof SummaryEvent) {
            point = logsEventToPointConverter.summaryEventConverter().convert((SummaryEvent) event);
        } else if (event instanceof BestSolutionEvent) {
            point = logsEventToPointConverter.bestSolutionEventConverter().convert((BestSolutionEvent) event);
        }

        if (point != null) {
            influxDataWriter.writePoint(evaluationLogsBucketName, point);
        }
    }

}
