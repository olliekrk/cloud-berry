package com.cloudberry.cloudberry.db.influx.util.converter;

import com.cloudberry.cloudberry.db.influx.InfluxCommonTags;
import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import com.cloudberry.cloudberry.util.syntax.MapSyntax;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogsEventToPointConverter {

    private final PointBuilder pointBuilder;

    public Converter<WorkplaceEvent, Point> workplaceEventConverter() {
        return event -> pointBuilder.buildPoint(
                event.getClass().getSimpleName(),
                event.getTime(),
                MapSyntax.updated(event.getParameters(), "workplaceId", event.getWorkplaceId()),
                Map.of(
                        InfluxCommonTags.EVALUATION_ID, event.getEvaluationId().toHexString()
                )
        );
    }

    public Converter<SummaryEvent, Point> summaryEventConverter() {
        return event -> pointBuilder.buildPoint(
                event.getClass().getSimpleName(),
                event.getTime(),
                Map.of(
                        "bestEvaluation", event.getBestEvaluation(),
                        "evaluationsCount", event.getEvaluationsCount()
                ),
                Map.of(
                        InfluxCommonTags.EVALUATION_ID, event.getEvaluationId().toHexString()
                )
        );
    }

    public Converter<BestSolutionEvent, Point> bestSolutionEventConverter() {
        return event -> pointBuilder.buildPoint(
                event.getClass().getSimpleName(),
                event.getTime(),
                MapSyntax.merged( // todo?
                        event.getSolution().parameters,
                        Map.of(

                                "occurrencesCount", event.getDetails().getOccurrencesCount(),
                                "stepNumber", event.getDetails().getStepNumber(),
                                "workplaceId", event.getDetails().getWorkplaceId()
                        )
                ),
                Map.of(
                        InfluxCommonTags.EVALUATION_ID, event.getEvaluationId().toHexString()
                )
        );
    }
}
