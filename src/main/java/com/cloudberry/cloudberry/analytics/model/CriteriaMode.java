package com.cloudberry.cloudberry.analytics.model;

public enum CriteriaMode {
    /**
     * Search for series with any value matching the criteria.
     */
    ANY,
    /**
     * Search for series with mean value matching the criteria.
     */
    AVERAGE,
    /**
     * Search for series with final value matching the criteria.
     */
    FINAL

}
