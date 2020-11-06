import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {TopologyId, TopologyNode} from "../model";
import {share} from "rxjs/operators";
import {TopologyNodeRestService} from "../rest/topology-node-rest.service";

@Injectable({
  providedIn: "root"
})
export class TopologyNodeApiService {

  constructor(private rest: TopologyNodeRestService) {
  }

  getTopologyNodes(topologyId: TopologyId): Observable<TopologyNode[]> {
    return this.rest.getTopologyNodes(topologyId).pipe(share());
  }
}
