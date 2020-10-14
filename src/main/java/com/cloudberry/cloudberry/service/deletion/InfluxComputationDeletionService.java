package com.cloudberry.cloudberry.service.deletion;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataRemover;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags.COMPUTATION_ID;

@Service
@RequiredArgsConstructor
public class InfluxComputationDeletionService {
    private final InfluxDataRemover influxDataRemover;

    public void deleteByComputationId(InfluxQueryFields influxQueryFields, List<ObjectId> computationIds) {
        influxDataRemover.deleteDataWithDifferentPossibleTagValues(
                influxQueryFields,
                COMPUTATION_ID, ListSyntax.mapped(computationIds, ObjectId::toString)
        );
    }

}
