import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Topology, TopologyId} from "../model";
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

  useTopology(topologyId: TopologyId): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/id/${topologyId}/use`, null);
  }

  deleteTopology(topologyId: TopologyId): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/id/${topologyId}/delete`, null);
  }

  createTopology(topologyName: string): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/create`, null, {params: {name: topologyName}});
  }
}
