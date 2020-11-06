export type TopologyId = string;
export type TopologyNodeId = string;

export interface Topology {
  id: TopologyId;
  name: string;
  userDefined: boolean;
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
