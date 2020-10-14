package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.time.Instant.ofEpochMilli;
import static java.time.Instant.ofEpochSecond;

public final class DataGenerator {

    //config experiment
    public static final ObjectId EXPERIMENT_ID_1 = ObjectId.get();
    public static final ObjectId EXPERIMENT_ID_2 = ObjectId.get();
    public static final ObjectId EXPERIMENT_ID_3_NO_CONFIGURATIONS = ObjectId.get();
    public static final String EXPERIMENT_NAME_1 = "experiment_test_1";
    public static final String EXPERIMENT_NAME_2 = "experiment_test_2";
    public static final String EXPERIMENT_NAME_3 = "no_configurations";
    public static final Map<String, Object> EXPERIMENT_PARAMS_1 = Map.of("price", 3000);
    public static final Experiment TEST_EXPERIMENT_1 =
            new Experiment(EXPERIMENT_ID_1, EXPERIMENT_NAME_1, EXPERIMENT_PARAMS_1, ofEpochMilli(100));
    public static final Map<String, Object> EXPERIMENT_PARAMS_2 = Map.of("param", 1);
    public static final Experiment TEST_EXPERIMENT_2 =
            new Experiment(EXPERIMENT_ID_2, EXPERIMENT_NAME_2, EXPERIMENT_PARAMS_2, ofEpochMilli(200));
    public static final Experiment TEST_EXPERIMENT_3_NO_CONFIGURATIONS =
            new Experiment(EXPERIMENT_ID_3_NO_CONFIGURATIONS, EXPERIMENT_NAME_3, Map.of(), ofEpochMilli(1000));
    public static final List<Experiment> ALL_EXPERIMENTS =
            List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2, TEST_EXPERIMENT_3_NO_CONFIGURATIONS);

    //config experiment configuration
    public static final ObjectId CONFIGURATION_ID_1 = ObjectId.get();
    public static final ObjectId CONFIGURATION_ID_2 = ObjectId.get();
    public static final ObjectId CONFIGURATION_ID_3 = ObjectId.get();
    public static final ObjectId CONFIGURATION_ID_4_NO_COMPUTATIONS = ObjectId.get();
    public static final String CONFIGURATION_FILE_NAME_1 = "config_1";
    public static final String CONFIGURATION_FILE_NAME_2 = "config_2";
    public static final String CONFIGURATION_FILE_NAME_3 = "config_3";
    public static final Map<String, Object> CONFIGURATION_PARAMS_1 = Map.of("price", 3000);
    public static final ExperimentConfiguration TEST_CONFIGURATION_1 =
            new ExperimentConfiguration(CONFIGURATION_ID_1, EXPERIMENT_ID_1, CONFIGURATION_FILE_NAME_1,
                    CONFIGURATION_PARAMS_1, ofEpochSecond(10));
    public static final Map<String, Object> CONFIGURATION_PARAMS_2 = Map.of();
    public static final ExperimentConfiguration TEST_CONFIGURATION_2 =
            new ExperimentConfiguration(CONFIGURATION_ID_2, EXPERIMENT_ID_1, CONFIGURATION_FILE_NAME_2,
                    CONFIGURATION_PARAMS_2, ofEpochSecond(20));
    public static final Map<String, Object> CONFIGURATION_PARAMS_3 = Map.of("owner", "Andrew");
    public static final ExperimentConfiguration TEST_CONFIGURATION_3 =
            new ExperimentConfiguration(CONFIGURATION_ID_3, EXPERIMENT_ID_2, CONFIGURATION_FILE_NAME_3,
                    CONFIGURATION_PARAMS_3, ofEpochSecond(30));
    public static final ExperimentConfiguration TEST_CONFIGURATION_4_NO_COMPUTATIONS =
            new ExperimentConfiguration(CONFIGURATION_ID_4_NO_COMPUTATIONS, EXPERIMENT_ID_2, CONFIGURATION_FILE_NAME_3,
                    CONFIGURATION_PARAMS_3, ofEpochSecond(30));
    public static final List<ExperimentConfiguration> ALL_CONFIGURATIONS =
            List.of(TEST_CONFIGURATION_1, TEST_CONFIGURATION_2, TEST_CONFIGURATION_3,
                    TEST_CONFIGURATION_4_NO_COMPUTATIONS);

    //config experiment computation
    public static final ObjectId COMPUTATION_ID_1 = ObjectId.get();
    public static final ObjectId COMPUTATION_ID_2 = ObjectId.get();
    public static final ObjectId COMPUTATION_ID_3 = ObjectId.get();
    public static final ObjectId COMPUTATION_ID_4 = ObjectId.get();
    public static final ExperimentComputation TEST_COMPUTATION_1 =
            new ExperimentComputation(COMPUTATION_ID_1, CONFIGURATION_ID_1, Instant.ofEpochSecond(1));
    public static final ExperimentComputation TEST_COMPUTATION_2 =
            new ExperimentComputation(COMPUTATION_ID_2, CONFIGURATION_ID_2, Instant.ofEpochSecond(10));
    public static final ExperimentComputation TEST_COMPUTATION_3 =
            new ExperimentComputation(COMPUTATION_ID_3, CONFIGURATION_ID_3, Instant.ofEpochSecond(100));
    public static final ExperimentComputation TEST_COMPUTATION_4 =
            new ExperimentComputation(COMPUTATION_ID_4, CONFIGURATION_ID_3, Instant.ofEpochSecond(100));
    public static final List<ExperimentComputation> ALL_COMPUTATIONS =
            List.of(TEST_COMPUTATION_1, TEST_COMPUTATION_2, TEST_COMPUTATION_3, TEST_COMPUTATION_4);
}
