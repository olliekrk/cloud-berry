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
    public static final ObjectId CONFIGURATION_ID_1_A = ObjectId.get();
    public static final ObjectId CONFIGURATION_ID_1_B = ObjectId.get();
    public static final ObjectId CONFIGURATION_ID_2_A = ObjectId.get();
    public static final ObjectId CONFIGURATION_ID_2_B_NO_COMPUTATIONS = ObjectId.get();

    public static final String CONFIGURATION_NAME_1_A = "config_1_A";
    public static final String CONFIGURATION_NAME_1_B = "config_1_B";
    public static final String CONFIGURATION_NAME_2_A = "config_2_A";
    public static final String CONFIGURATION_NAME_2_B = "config_2_B";

    public static final ExperimentConfiguration TEST_CONFIGURATION_1_A =
            new ExperimentConfiguration(CONFIGURATION_ID_1_A, EXPERIMENT_ID_1, CONFIGURATION_NAME_1_A,
                                        Map.of("price", 3000), ofEpochSecond(10)
            );
    public static final ExperimentConfiguration TEST_CONFIGURATION_1_B =
            new ExperimentConfiguration(CONFIGURATION_ID_1_B, EXPERIMENT_ID_1, CONFIGURATION_NAME_1_B,
                                        Map.of(), ofEpochSecond(20)
            );

    public static final ExperimentConfiguration TEST_CONFIGURATION_2_A =
            new ExperimentConfiguration(CONFIGURATION_ID_2_A, EXPERIMENT_ID_2, CONFIGURATION_NAME_2_A,
                                        Map.of("owner", "Andrew"), ofEpochSecond(30)
            );
    public static final ExperimentConfiguration TEST_CONFIGURATION_2_B_NO_COMPUTATIONS =
            new ExperimentConfiguration(CONFIGURATION_ID_2_B_NO_COMPUTATIONS, EXPERIMENT_ID_2,
                                        CONFIGURATION_NAME_2_B, Map.of(), ofEpochSecond(30)
            );

    public static final List<ExperimentConfiguration> ALL_CONFIGURATIONS =
            List.of(
                    TEST_CONFIGURATION_1_A,
                    TEST_CONFIGURATION_1_B,
                    TEST_CONFIGURATION_2_A,
                    TEST_CONFIGURATION_2_B_NO_COMPUTATIONS
            );

    //config experiment computation
    public static final ObjectId COMPUTATION_ID_1_A_A = ObjectId.get();
    public static final ObjectId COMPUTATION_ID_1_B_A = ObjectId.get();
    public static final ObjectId COMPUTATION_ID_2_A_A = ObjectId.get();
    public static final ObjectId COMPUTATION_ID_2_A_B = ObjectId.get();
    public static final ExperimentComputation TEST_COMPUTATION_1_A_A =
            new ExperimentComputation(COMPUTATION_ID_1_A_A, CONFIGURATION_ID_1_A, Instant.ofEpochSecond(1));
    public static final ExperimentComputation TEST_COMPUTATION_1_B_A =
            new ExperimentComputation(COMPUTATION_ID_1_B_A, CONFIGURATION_ID_1_B, Instant.ofEpochSecond(10));

    public static final ExperimentComputation TEST_COMPUTATION_2_A_A =
            new ExperimentComputation(COMPUTATION_ID_2_A_A, CONFIGURATION_ID_2_A, Instant.ofEpochSecond(100));
    public static final ExperimentComputation TEST_COMPUTATION_2_A_B =
            new ExperimentComputation(COMPUTATION_ID_2_A_B, CONFIGURATION_ID_2_A, Instant.ofEpochSecond(100));
    public static final List<ExperimentComputation> ALL_COMPUTATIONS =
            List.of(TEST_COMPUTATION_1_A_A, TEST_COMPUTATION_1_B_A, TEST_COMPUTATION_2_A_A, TEST_COMPUTATION_2_A_B);
}
