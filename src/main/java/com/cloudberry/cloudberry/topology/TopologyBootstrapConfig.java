package com.cloudberry.cloudberry.topology;

import com.cloudberry.cloudberry.topology.exception.TopologyException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import com.cloudberry.cloudberry.topology.service.bootstrap.DefaultTopologyProvider;
import com.cloudberry.cloudberry.topology.service.bootstrap.TopologyBootstrapper;
import com.cloudberry.cloudberry.topology.service.bootstrap.TopologyInitializer;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TopologyBootstrapConfig {
    private final TopologyBootstrapper topologyBootstrapper;
    private final TopologyInitializer topologyInitializer;
    private final TopologyService topologyService;
    private final DefaultTopologyProvider defaultTopologyProvider;

    @Bean
    public KafkaStreams kafkaStreams() {
        topologyInitializer.purgeDefaults();
        var topology = topologyService
                .findAny()
                .filter(Topology::isValid)
                .orElseGet(() -> {
                    log.warn("No overridden topology configuration was found");
                    log.warn("Retrying bootstrapping with predefined topology");
                    return topologyInitializer.saveTopologySetupData(defaultTopologyProvider.get());
                });
        return configureWithTopology(topology);
    }

    private KafkaStreams configureWithTopology(Topology topology) {
        return Try.of(() -> {
            var streams = topologyBootstrapper.bootstrapStreams(topology);
            streams.start();
            return streams;
        }).onFailure(
                TopologyException.class,
                e -> log.error("Bootstrapping has failed due to a topology exception:", e)
        ).onFailure(
                e -> log.error("Bootstrapping has failed due to an unknown exception:", e)
        ).getOrNull();
    }

}
