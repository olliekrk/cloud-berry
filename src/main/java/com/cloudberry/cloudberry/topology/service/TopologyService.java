package com.cloudberry.cloudberry.topology.service;

import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.repository.TopologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopologyService {
    private final TopologyRepository topologyRepository;

    public Topology save(Topology topology) {
        return topologyRepository.save(topology);
    }

    public Optional<Topology> findAny() {
        return topologyRepository.findAll().stream().findAny();
    }
}
