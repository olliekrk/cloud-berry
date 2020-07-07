package com.cloudberry.cloudberry.db.mongo.data.logs;

import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * S tag from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Document(collection = "summary_log")
public class MongoSummaryLog extends MongoLog {
    double bestEvaluation;
    long evaluationsCount;

    public MongoSummaryLog(Instant time, ObjectId evaluationId, double bestEvaluation, long evaluationsCount) {
        super(time, evaluationId);
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }

    @Override
    public final MongoLogType getType() {
        return MongoLogType.SUMMARY;
    }

    public static MongoSummaryLog ofEvent(SummaryEvent event) {
        return new MongoSummaryLog(event.getTime(), event.getEvaluationId(), event.getBestEvaluation(), event.getEvaluationsCount());
    }
}
