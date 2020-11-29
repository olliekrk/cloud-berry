export type TopologyId = string;
export type TopologyNodeId = string;

export interface Topology {
  id: TopologyId;
  name: string;
  userDefined: boolean;
  valid: boolean;
  edges: { [source: string]: TopologyEdge[] };
}

export interface TopologyEdge {
  source: TopologyNodeId;
  target: TopologyNodeId;
  name: string;
}

export interface TopologyNode {
  id: TopologyNodeId;
  nodeType: TopologyNodeType;
  name: string;
}

export interface TopologyData {
  topology: Topology;
  topologyNodes: TopologyNode[];
}

export enum TopologyNodeType {
  Root = "Root",
  Sink = "Sink",
  Filter = "Filter",
  Map = "Map",
  Counter = "Counter",
  Merge = "Merge",
}

export type FilterExpression = object;
export type MappingExpression = object;

export const topologyNodeTypeRequiredFields: Record<TopologyNodeType, string[]> = {
  [TopologyNodeType.Root]: ["name", "inputTopicName"],
  [TopologyNodeType.Sink]: ["name", "outputBucketName"],
  [TopologyNodeType.Filter]: ["name", "filterExpression"],
  [TopologyNodeType.Map]: ["name", "mappingExpression"],
  [TopologyNodeType.Counter]: ["name", "metricName"],
  [TopologyNodeType.Merge]: ["name"],
};
