package com.cloudberry.cloudberry.topology.service;

import com.cloudberry.cloudberry.topology.exception.TopologyNotFoundException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.repository.TopologyRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopologyService {
    private final TopologyRepository topologyRepository;
    private final static Sort sortByName = Sort.by("name");

    public Topology save(Topology topology) {
        return topologyRepository.save(topology);
    }

    public Optional<Topology> findAny() {
        return topologyRepository.findAll(sortByName).stream().findAny();
    }

    public List<Topology> findAll() {
        return topologyRepository.findAll(sortByName);
    }

    public Optional<Topology> findById(ObjectId id) {
        return topologyRepository.findById(id);
    }

    public Topology findByIdOrThrow(ObjectId id) {
        return findById(id).orElseThrow(() -> new TopologyNotFoundException(id));
    }

    public List<Topology> findByName(String topologyName) {
        return topologyRepository.findByName(topologyName);
    }

    public List<Topology> findDefaultTopologies() {
        return topologyRepository.findByUserDefinedIsFalse();
    }

    public void deleteById(ObjectId topologyId) {
        topologyRepository.deleteById(topologyId);
    }

    public List<Topology> deleteAllByIds(List<ObjectId> ids) {
        return topologyRepository.deleteAllByIdIn(ids);
    }

    public boolean isNodeUsedAnywhere(ObjectId nodeId) {
        return topologyRepository.findAll().stream().anyMatch(topology -> topology.getEdges().containsKey(nodeId));
    }
}
