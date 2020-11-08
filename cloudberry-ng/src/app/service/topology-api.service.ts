import {Injectable} from "@angular/core";
import {TopologyRestService} from "../rest/topology-rest.service";
import {Observable} from "rxjs";
import {Topology, TopologyId} from "../model";
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

  getAvailableTopologies(): Observable<Topology[]> {
    return this.rest.getAvailableTopologies().pipe(share());
  }

  useTopology(topologyId: TopologyId): Observable<void> {
    return this.rest.useTopology(topologyId).pipe(share());
  }

  deleteTopology(topologyId: TopologyId): Observable<void> {
    return this.rest.deleteTopology(topologyId).pipe(share());
  }

  createTopology(topologyName: string) {
    return this.rest.createTopology(topologyName).pipe(share());
  }
}
