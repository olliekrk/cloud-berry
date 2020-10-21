package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.google.common.base.Suppliers;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxOrganizationService {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    private final Supplier<String> defaultOrganizationId =
            Suppliers.memoizeWithExpiration(this::setupDefaultOrganizationId, 4, TimeUnit.HOURS);

    public String getDefaultOrganizationId() {
        return defaultOrganizationId.get();
    }

    /**
     * Fetch organization ID from Influx instance using organization name from application config.
     */
    private String setupDefaultOrganizationId() {
        var defaultOrganizationOpt = influxClient
                .getOrganizationsApi()
                .findOrganizations()
                .stream()
                .filter(organization -> organization.getName().equals(influxConfig.getDefaultOrganizationName()))
                .findFirst();

        return defaultOrganizationOpt
                .map(Organization::getId)
                .orElseGet(() -> {
                    var organizationId = createNewDefaultOrganization().getId();
                    log.warn("No default organization could be fetched from InfluxDB. " +
                                     "Verify the application configuration files. " +
                                     "New default organization has been created: {}", organizationId);
                    return organizationId;
                });
    }

    private Organization createNewDefaultOrganization() {
        return influxClient.getOrganizationsApi().createOrganization(influxConfig.getDefaultOrganizationName());
    }

}
