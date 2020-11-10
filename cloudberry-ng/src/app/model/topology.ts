export type TopologyId = string;
export type TopologyNodeId = string;

export interface Topology {
  id: TopologyId;
  name: string;
  userDefined: boolean;
  valid: boolean;
  edges: { [source: string]: TopologyNodeId[] };
}

export interface TopologyNode {
  id: TopologyNodeId;
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
  Counter = "Counter"
}
