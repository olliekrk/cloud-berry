package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxOrganizationService {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    private Optional<String> defaultOrganizationId = Optional.empty();

    private String setupDefaultOrganizationId() {
        var defaultOrganizationOpt = influxClient
                .getOrganizationsApi()
                .findOrganizations()
                .stream()
                .filter(organization -> organization.getName().equals(influxConfig.getDefaultOrganizationName()))
                .findFirst();

        var defaultOrganizationId = defaultOrganizationOpt
                .map(Organization::getId)
                .orElseGet(() -> {
                    var id = influxConfig.getDefaultOrganizationId();
                    log.warn("No default organization could be fetched from InfluxDB. " +
                            "Verify the application configuration files. " +
                            "Using default organization ID from configuration (unsafe): " + id);
                    return id;
                });

        this.defaultOrganizationId = Optional.of(defaultOrganizationId);
        return defaultOrganizationId;
    }

    /**
     * Lazy method to fetch organization ID from Influx instance using organization name from application config.
     */
    public String getDefaultOrganizationId() {
        return defaultOrganizationId.orElseGet(this::setupDefaultOrganizationId);
    }
}
