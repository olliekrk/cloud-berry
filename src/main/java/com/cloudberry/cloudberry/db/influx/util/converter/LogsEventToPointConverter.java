package com.cloudberry.cloudberry.db.influx.util.converter;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogsEventToPointConverter {

    private final PointBuilder pointBuilder;

    public Converter<WorkplaceEvent, Point> workplaceEventConverter() {
        return event -> {
            var fields = new HashMap<>(event.getParameters());
            fields.put(WorkplaceEvent.Fields.WORKPLACE_ID, event.getWorkplaceId());

            return pointBuilder.buildPoint(
                    event.getClass().getSimpleName(),
                    event.getTime(),
                    fields,
                    Map.of(InfluxDefaults.CommonTags.COMPUTATION_ID, event.getComputationId().toHexString())
            );
        };
    }

    public Converter<SummaryEvent, Point> summaryEventConverter() {
        return event -> {
            Map<String, Object> fields = Map.of(
                    SummaryEvent.Fields.BEST_COMPUTATION, event.getBestComputation(),
                    SummaryEvent.Fields.COMPUTATIONS_COUNT, event.getComputationsCount()
            );
            return pointBuilder.buildPoint(
                    event.getClass().getSimpleName(),
                    event.getTime(),
                    fields,
                    Map.of(InfluxDefaults.CommonTags.COMPUTATION_ID, event.getComputationId().toHexString())
            );
        };
    }

    public Converter<BestSolutionEvent, Point> bestSolutionEventConverter() {
        return event -> {
            var fields = new HashMap<>(event.getSolution().getParameters());
            fields.put(BestSolutionEvent.Fields.OCCURRENCES_COUNT, event.getDetails().getOccurrencesCount());
            fields.put(BestSolutionEvent.Fields.STEP_NUMBER, event.getDetails().getStepNumber());
            fields.put(BestSolutionEvent.Fields.WORKPLACE_ID, event.getDetails().getWorkplaceId());

            return pointBuilder.buildPoint(
                    event.getClass().getSimpleName(),
                    event.getTime(),
                    fields,
                    Map.of(InfluxDefaults.CommonTags.COMPUTATION_ID, event.getComputationId().toHexString())
            );
        };
    }
}
