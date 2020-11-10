import {Injectable} from "@angular/core";
import {TopologyRestService} from "../rest/topology-rest.service";
import {Observable} from "rxjs";
import {Topology, TopologyId, TopologyNodeId} from "../model";
import {share} from "rxjs/operators";

@Injectable({
  providedIn: "root"
})
export class TopologyApiService {

  constructor(private rest: TopologyRestService) {
  }

  getActiveTopology(): Observable<Topology | null> {
    return this.rest.getActiveTopology().pipe(share());
  }

  getTopology(topologyId: TopologyId): Observable<Topology | null> {
    return this.rest.getTopology(topologyId).pipe(share());
  }

  getAvailableTopologies(): Observable<Topology[]> {
    return this.rest.getAvailableTopologies().pipe(share());
  }

  useTopology(topologyId: TopologyId): Observable<void> {
    return this.rest.useTopology(topologyId).pipe(share());
  }

  deleteTopology(topologyId: TopologyId): Observable<void> {
    return this.rest.deleteTopology(topologyId).pipe(share());
  }

  createTopology(topologyName: string): Observable<void> {
    return this.rest.createTopology(topologyName).pipe(share());
  }

  deleteEdge(topologyId: TopologyId, sourceId: TopologyNodeId, targetId: TopologyNodeId): Observable<void> {
    return this.rest.deleteEdge(topologyId, sourceId, targetId).pipe(share());
  }

  addEdgeToTopology(topologyId: TopologyId, sourceNodeId: TopologyNodeId, targetNodeId: TopologyNodeId,
                    addVertexToTopologyIfNotAdded: boolean = true): Observable<void> {
    return this.rest.addEdgeToTopology(topologyId, sourceNodeId, targetNodeId, addVertexToTopologyIfNotAdded);
  }

  addNodeBetweenNodes(topologyId: TopologyId, sourceNodeId: TopologyNodeId, insertedNodeId: TopologyNodeId, targetNodeId: TopologyNodeId,
                      addVertexToTopologyIfNotAdded: boolean = true): Observable<void> {
    return this.rest.addNodeBetweenNodes(topologyId, sourceNodeId, insertedNodeId, targetNodeId, addVertexToTopologyIfNotAdded);
  }

  deleteNodeFromTopology(topologyId: TopologyId, nodeId: TopologyNodeId): Observable<void> {
    return this.rest.deleteNode(topologyId, nodeId).pipe(share());
  }
}
