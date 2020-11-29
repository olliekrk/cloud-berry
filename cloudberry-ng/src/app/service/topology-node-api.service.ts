import {Injectable} from "@angular/core";
import {EMPTY, Observable} from "rxjs";
import {TopologyId, TopologyNode, TopologyNodeType} from "../model";
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

  addCounterNode(nodeName: string, metricName: string): Observable<TopologyNode> {
    console.log(nodeName, metricName);
    return this.rest.createCounterNode(nodeName, metricName).pipe(share());
  }

  createNode(nodeType: TopologyNodeType, json: Record<string, any>): Observable<TopologyNode> {
    switch (nodeType) {
      case TopologyNodeType.Counter:
        return this.rest.createCounterNode(json.name, json.metricName).pipe(share());
      case TopologyNodeType.Filter:
        return this.rest.createFilterNode(json.name, json.filterExpression).pipe(share());
      case TopologyNodeType.Map:
        return this.rest.createMapNode(json.name, json.mappingExpression).pipe(share());
      case TopologyNodeType.Root:
        return this.rest.createRootNode(json.name, json.inputTopicName).pipe(share());
      case TopologyNodeType.Sink:
        return this.rest.createSinkNode(json.name, json.outputBucketName).pipe(share());
      case TopologyNodeType.Merge:
        return this.rest.createMergeNode(json.name).pipe(share());
      case TopologyNodeType.Branch:
        return this.rest.createBranchNode(json.name, json.filterExpression).pipe(share());
      default:
        return EMPTY;
    }
  }
}
