import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {TopologyId, TopologyNode} from "../model";

@Injectable({
  providedIn: "root"
})
export class TopologyNodeRestService {

  readonly baseUrl: string = `${environment.apiUrl}/topologyNode`;

  constructor(private httpClient: HttpClient) {
  }

  getTopologyNodes(topologyId: TopologyId): Observable<TopologyNode[]> {
    return this.httpClient.get<TopologyNode[]>(`${this.baseUrl}/topology/${topologyId}`);
  }

}
