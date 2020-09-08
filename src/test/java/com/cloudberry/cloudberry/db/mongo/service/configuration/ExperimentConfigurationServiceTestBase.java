package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.ExperimentConfigurationService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Map;

@Import(ExperimentConfigurationService.class)
@EmbeddedMongoTest
abstract class ExperimentConfigurationServiceTestBase {
    public static final ObjectId EXPERIMENT_ID = ObjectId.get();
    public static final String CONFIGURATION_FILE_NAME = "fileName";
    public static final Map<String, Object> PARAMETERS = Map.of("price", 3000);
    protected static final ExperimentConfiguration TEST_CONFIGURATION = new ExperimentConfiguration(ObjectId.get(), EXPERIMENT_ID, CONFIGURATION_FILE_NAME, PARAMETERS, Instant.ofEpochSecond(10));

    @Autowired
    protected ExperimentConfigurationService experimentConfigurationService;
    @Autowired
    protected ConfigurationRepository configurationRepository;
    @Autowired
    protected ExperimentRepository experimentRepository;

    @BeforeEach
    void cleanDatabase() {
        configurationRepository.deleteAll().block();
        experimentRepository.deleteAll().block();
    }

    protected void assertExperimentInDatabase(ExperimentConfiguration experiment) {
        final var foundExperiment = configurationRepository.findById(experiment.getId()).block();
        Assertions.assertEquals(experiment, foundExperiment);
    }
}
