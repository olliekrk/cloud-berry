package com.cloudberry.cloudberry.model;

/**
 * Predefined known parameters keys to be used in more domain-specific data transformations.
 */
public interface KnownParameters {

    enum ConfigurationParameters {
        WORKPLACES_COUNT("workplaces.count"),
        PROBLEM_SIZE("problem.size"),
        POPULATION_SIZE("population.size");

        public final String key;

        ConfigurationParameters(String key) {
            this.key = key;
        }
    }
}
