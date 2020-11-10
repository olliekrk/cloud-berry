import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Topology, TopologyId, TopologyNodeId} from "../model";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: "root"
})
export class TopologyRestService {

  readonly baseUrl: string = `${environment.apiUrl}/topology`;

  constructor(private httpClient: HttpClient) {

  }

  getActiveTopology(): Observable<Topology | null> {
    return this.httpClient.get<Topology | null>(this.baseUrl + "/active");
  }

  getAvailableTopologies(): Observable<Topology[]> {
    return this.httpClient.get<Topology[]>(this.baseUrl + "/all");
  }

  getTopology(topologyId: TopologyId): Observable<Topology | null> {
    return this.httpClient.get<Topology | null>(`${this.baseUrl}/id/${topologyId}`);
  }

  useTopology(topologyId: TopologyId): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/id/${topologyId}/use`, null);
  }

  deleteTopology(topologyId: TopologyId): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/id/${topologyId}/delete`, null);
  }

  createTopology(topologyName: string): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/create`, null, {params: {name: topologyName}});
  }

  deleteEdge(topologyId: TopologyId, sourceId: TopologyNodeId, targetId: TopologyNodeId): Observable<void> {
    const params = {sourceNodeIdHex: sourceId, targetNodeIdHex: targetId};
    return this.httpClient.delete<void>(`${this.baseUrl}/id/${topologyId}/deleteEdge`, {params});
  }

  addEdgeToTopology(topologyId: TopologyId, sourceNodeId: TopologyNodeId, targetNodeId: TopologyNodeId,
                    addVertexToTopologyIfNotAdded: boolean): Observable<void> {
    const params = new HttpParams()
      .set("sourceNodeIdHex", sourceNodeId)
      .set("targetNodeIdHex", targetNodeId)
      .set("addVertexToTopologyIfNotAdded", addVertexToTopologyIfNotAdded.toString());
    return this.httpClient.put<void>(`${this.baseUrl}/id/${topologyId}/addEdge`, null, {params});
  }

  addNodeBetweenNodes(topologyId: TopologyId, sourceNodeId: TopologyNodeId, insertedNodeId: TopologyNodeId,
                      targetNodeId: TopologyNodeId,
                      addVertexToTopologyIfNotAdded: boolean): Observable<void> {
    const params = new HttpParams()
      .set("sourceNodeIdHex", sourceNodeId)
      .set("insertedNodeIdHex", insertedNodeId)
      .set("targetNodeIdHex", targetNodeId)
      .set("addVertexToTopologyIfNotAdded", addVertexToTopologyIfNotAdded.toString());
    return this.httpClient.put<void>(`${this.baseUrl}/id/${topologyId}/addNodeBetweenNodes`, null, {params});
  }
}
