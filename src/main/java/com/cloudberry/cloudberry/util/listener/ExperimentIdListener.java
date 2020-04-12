package com.cloudberry.cloudberry.util.listener;

import com.cloudberry.cloudberry.model.metadata.Experiment;
import com.cloudberry.cloudberry.util.IdSequenceGenerator;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class ExperimentIdListener extends AbstractMongoEventListener<Experiment> {
    final IdSequenceGenerator idGenerator;

    public ExperimentIdListener(IdSequenceGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Experiment> event) {
        Long sourceId = event.getSource().getId();
        if (sourceId == null || sourceId < 1) {
            event.getSource().setId(idGenerator.nextId("id_" + Experiment.class.getSimpleName()));
        }
    }
}
