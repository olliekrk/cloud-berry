import {Injectable} from "@angular/core";
import {TopologyRestService} from "../rest/topology-rest.service";
import {Observable} from "rxjs";
import {Topology} from "../model";
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

}
