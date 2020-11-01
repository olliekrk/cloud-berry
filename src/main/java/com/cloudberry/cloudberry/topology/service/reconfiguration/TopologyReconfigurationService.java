package com.cloudberry.cloudberry.topology.service.reconfiguration;

import com.cloudberry.cloudberry.topology.exception.TopologyException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import com.cloudberry.cloudberry.topology.service.bootstrap.TopologyBootstrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopologyReconfigurationService {
    private final TopologyService topologyService;
    private final TopologyBootstrapper topologyBootstrapper;

    private final AtomicReference<KafkaStreams> streamsRef = new AtomicReference<>();
    private final AtomicReference<Thread> streamsShutdownHookRef = new AtomicReference<>();

    public void useTopology(ObjectId topologyId) {
        useTopology(topologyService.findByIdOrThrow(topologyId));
    }

    public KafkaStreams useTopology(Topology topology) {
        try {
            shutdownOldStreams();
            var streams = topologyBootstrapper.bootstrapStreams(topology);
            setupNewStreams(streams);
            return streams;
        } catch (TopologyException e) {
            log.error("Bootstrapping has failed due to a topology exception:", e);
            return null;
        }
    }

    private void shutdownOldStreams() {
        var oldStreams = streamsRef.getAndSet(null);
        var oldShutdownHook = streamsShutdownHookRef.getAndSet(null);
        if (oldStreams != null) {
            oldStreams.close();
            oldStreams.cleanUp();
        }
        if (oldShutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(oldShutdownHook);
        }
    }

    private void setupNewStreams(KafkaStreams streams) {
        var runtime = Runtime.getRuntime();
        var shutdownHook = new Thread(streams::close);

        streamsRef.set(streams);
        streamsShutdownHookRef.set(shutdownHook);

        streams.start();
        runtime.addShutdownHook(shutdownHook);
    }

}
