package com.cloudberry.cloudberry.topology.service;

import com.cloudberry.cloudberry.topology.exception.NodeNotFoundException;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.repository.TopologyNodeRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopologyNodeService {
    private final TopologyNodeRepository topologyNodeRepository;

    public TopologyNode save(TopologyNode node) {
        return topologyNodeRepository.save(node);
    }

    public List<TopologyNode> saveAll(Collection<TopologyNode> nodes) {
        return topologyNodeRepository.saveAll(nodes);
    }

    public Optional<TopologyNode> getOpt(ObjectId id) {
        return topologyNodeRepository.findById(id);
    }

    public TopologyNode get(ObjectId id) {
        return topologyNodeRepository.findById(id).orElseThrow(() -> new NodeNotFoundException(id));
    }

    public List<TopologyNode> findAll() {
        return topologyNodeRepository.findAll();
    }

    public Optional<TopologyNode> findById(ObjectId id) {
        return topologyNodeRepository.findById(id);
    }

    public List<TopologyNode> findAllByName(String name) {
        return topologyNodeRepository.findAllByName(name);
    }

    public List<TopologyNode> removeByIds(List<ObjectId> ids) {
        return topologyNodeRepository.deleteAllByIdIn(ids);
    }
}
