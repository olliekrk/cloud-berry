package com.cloudberry.cloudberry.topology.config;

import com.cloudberry.cloudberry.topology.service.bootstrap.TopologyInitializationService;
import com.cloudberry.cloudberry.topology.service.reconfiguration.TopologyReconfigurationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TopologyBootstrapConfig {
    private final TopologyInitializationService topologyInitializationService;
    private final TopologyReconfigurationService topologyReconfigurationService;

    @Bean
    public KafkaStreams kafkaStreams() {
        var topology = topologyInitializationService.getInitialTopology();
        return topologyReconfigurationService.useTopology(topology);
    }

}
