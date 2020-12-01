import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {DeletionExpression, FilterExpression, MappingExpression, TopologyId, TopologyNode} from "../model";

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

  createRootNode(name: string, inputTopicName: string): Observable<TopologyNode> {
    const params = {name, inputTopicName};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/root`, null, {params});
  }

  createSinkNode(name: string, outputBucketName: string): Observable<TopologyNode> {
    const params = {name, outputBucketName};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/sink`, null, {params});
  }

  createCounterNode(name: string, metricName: string): Observable<TopologyNode> {
    const params = {name, metricName};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/counter`, null, {params});
  }

  createMapNode(name: string, mappingExpression: MappingExpression): Observable<TopologyNode> {
    const params = {name};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/map`, mappingExpression, {params});
  }

  createFilterNode(name: string, filterExpression: FilterExpression): Observable<TopologyNode> {
    const params = {name};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/filter`, filterExpression, {params});
  }

  createMergeNode(name: string): Observable<TopologyNode> {
    const params = {name};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/merge`, null, {params});
  }

  createBranchNode(name: string, filterExpression: FilterExpression): Observable<TopologyNode> {
    const params = {name};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/branch`, filterExpression, {params});
  }

  createDeletionNode(name: string, deletionExpression: DeletionExpression): Observable<TopologyNode> {
    const params = {name};
    return this.httpClient.post<TopologyNode>(`${this.baseUrl}/deletion`, deletionExpression, {params});
  }
}
