package com.cloudberry.cloudberry.db.mongo.data.logs;

import com.cloudberry.cloudberry.kafka.event.EventType;
import com.cloudberry.cloudberry.kafka.event.SummaryEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * S tag from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Document(collection = "summary_log")
public class MongoSummaryLog extends MongoLog {
    double bestEvaluation;
    long evaluationsCount;

    public MongoSummaryLog(Instant time, UUID evaluationId, double bestEvaluation, long evaluationsCount) {
        super(time, evaluationId);
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }

    @Override
    public final EventType getType() {
        return EventType.SUMMARY;
    }

    public static MongoSummaryLog ofEvent(SummaryEvent event) {
        return new MongoSummaryLog(event.getTime(), event.getEvaluationId(), event.getBestEvaluation(), event.getEvaluationsCount());
    }
}
