import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
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

  addCounterNode(nodeName: string, metricName: string): Observable<TopologyNode> {
    const params = new HttpParams()
      .set("name", nodeName)
      .set("metricName", metricName);

    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/counter`, null, {params});
  }

}
