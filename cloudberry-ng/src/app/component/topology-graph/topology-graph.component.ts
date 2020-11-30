import {Component, ElementRef, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from "@angular/core";
import {BranchOutput, TopologyData, TopologyNode, TopologyNodeId, TopologyNodeType} from "../../model";
import {notNull, TypedSimpleChange} from "../../util";
import {MatDialog} from "@angular/material/dialog";
import {TopologyNodeDetailsInfoDialogComponent} from "../node-info-dialog/topology-node-details-info-dialog.component";
import * as cytoscape from "cytoscape";
import cxtmenu from "cytoscape-cxtmenu";
import dagre from "cytoscape-dagre";
import edgehandles from "cytoscape-edgehandles";
import {TopologyNodeApiService} from "../../service/topology-node-api.service";
import {TopologyApiService} from "../../service/topology-api.service";
import {AddNodeDialogComponent, AddNodeDialogResult} from "../add-node-dialog/add-node-dialog.component";
import {cyStylesheets, nodeDefaultColor, nodesColors} from "./cytoscape-styles";
import {mapTo, pluck, switchMap} from "rxjs/operators";
import {AddCounterNodeDialogComponent, AddCounterNodeDialogResult} from "../add-counter-node-dialog/add-counter-node-dialog.component";
import {SelectEdgeDialogComponent} from "../select-edge-dialog/select-edge-dialog.component";
import {EMPTY, Observable, of} from "rxjs";

cytoscape.use(edgehandles);
cytoscape.use(dagre);
cytoscape.use(cxtmenu);

type ComponentSimpleChanges = SimpleChanges & {
  topologyData?: TypedSimpleChange<TopologyData>;
};

@Component({
  selector: "app-topology-graph",
  templateUrl: "./topology-graph.component.html",
  styleUrls: ["./topology-graph.component.scss"]
})
export class TopologyGraphComponent implements OnInit, OnChanges {

  @ViewChild("cy", {static: true}) cyContainer: ElementRef;

  @Input() topologyData: TopologyData;
  @Output() topologyModified: EventEmitter<void> = new EventEmitter<void>();

  cyCore?: cytoscape.Core & any;

  readonly layoutOptions = {
    name: "dagre",
    rankDir: "LR",
    animate: true,
  };

  constructor(private dialog: MatDialog,
              private topologyNodeApiService: TopologyNodeApiService,
              private topologyApiService: TopologyApiService) {
  }

  ngOnInit(): void {
    this.initializeCytoscape();
  }

  ngOnChanges({topologyData}: ComponentSimpleChanges): void {
    if (topologyData?.currentValue) {
      this.fillGraph(topologyData.currentValue);
    }
  }

  openAddNodeDialog(copiedNode?: TopologyNode): void {
    this.dialog.open(AddNodeDialogComponent, {data: {copiedNode}})
      .afterClosed()
      .pipe(
        notNull(),
        switchMap(({nodeType, json}: AddNodeDialogResult) =>
          this.topologyNodeApiService
            .createNode(nodeType, json)
            .pipe(switchMap(node => this.topologyApiService.addNode(this.topologyData.topology.id, node.id)))
        )
      )
      .subscribe(() => this.emitTopologyModified());
  }

  private initializeCytoscape(): void {
    if (this.cyContainer) {
      this.cyCore = cytoscape({
        container: this.cyContainer.nativeElement,
        elements: {
          nodes: [],
          edges: [],
        },
        style: cyStylesheets,
        layout: this.layoutOptions
      });
      this.cyCore.cxtmenu(this.getCxtMenuNodeConfig());
      this.cyCore.cxtmenu(this.getCxtMenuEdgeConfig());
      this.cyCore.edgehandles(this.getEdgeHandlesConfig());
    }
  }

  private getCxtMenuNodeConfig(): any {
    return {
      menuRadius: 80,
      selector: "node", // elements matching this Cytoscape.js selector will trigger cxtmenus
      commands: [
        {
          content: "Show details",
          select: node => {
            const topologyNode = this.findNodeById(node.id());
            if (topologyNode) {
              this.dialog.open(TopologyNodeDetailsInfoDialogComponent, {data: {topologyNode}});
            }
          }
        },
        {
          content: "Delete",
          select: node => this.deleteNode(node),
        },
        {
          content: "Copy",
          select: node => this.copyNode(node),
        },
      ],
      openMenuEvents: "cxttap", // cytoscape events that will open the menu (space separated)
    };
  }

  private getCxtMenuEdgeConfig(): any {
    return {
      menuRadius: 80,
      selector: "edge", // elements matching this Cytoscape.js selector will trigger cxtmenus
      commands: [
        {
          content: "Add counter node",
          select: edge => this.addCounterNode(edge),
        },
        {
          content: "Delete this edge",
          select: edge => this.deleteEdge(edge),
        }
      ],
      openMenuEvents: "cxttap", // cytoscape events that will open the menu (space separated)
    };
  }

  private getEdgeHandlesConfig(): any {
    return {
      loopAllowed: () => false,
      handleNodes: source => this.isEdgeSourceValid(source), // whether node can be start of an edge
      edgeType: (source, target) => this.isEdgeValid(source, target) ? "flat" : null, // null = edge cannot be created
      snap: true,
      complete: (source, target, added) => { // after an edge is added
        this.cyCore.remove(`edge[id="${added.id()}"]`); // remove element added by cytoscape.js by default
        this.addEdge(source, target);
      }
    };
  }

  private fillGraph({topology, topologyNodes}: TopologyData): void {
    const nodes: cytoscape.NodeDefinition[] = topologyNodes.map(node => ({
      data: {
        id: node.id,
        name: node.name,
        bgColor: nodesColors[node.nodeType] || nodeDefaultColor,
      }
    }));

    const edgesArrays: cytoscape.EdgeDefinition[][] = Object.entries(topology.edges)
      .map(([source, topologyEdges]) => topologyEdges
        .map(edge => ({
          data: {
            id: `${edge.source}_${edge.target}_${edge.name}`,
            source: edge.source,
            target: edge.target,
            name: edge.name,
          },
          classes: "cy-edge",
        }))
      );

    const edges: cytoscape.EdgeDefinition[] = [].concat.apply([], edgesArrays);

    if (this.cyCore) {
      this.cyCore.nodes().remove();
      this.cyCore.add(nodes);
      this.cyCore.edges().remove();
      this.cyCore.add(edges);
      this.resetGraphLayout();
    }
  }

  private addCounterNode(edge: any): void {
    this.dialog.open(AddCounterNodeDialogComponent)
      .afterClosed()
      .pipe(
        notNull(),
        switchMap(({name, metricName}: AddCounterNodeDialogResult) =>
          this.topologyNodeApiService
            .addCounterNode(name, metricName)
            .pipe(
              switchMap(node => this.topologyApiService.addNode(this.topologyData.topology.id, node.id).pipe(mapTo(node.id))),
              switchMap(nodeId => {
                  const topologyId = this.topologyData.topology.id;
                  const sourceId = edge.source().id();
                  const targetId = edge.target().id();
                  return this.topologyApiService
                    .addNodeBetweenNodes(topologyId, sourceId, nodeId, targetId, true);
                }
              ))
        )
      )
      .subscribe(() => this.emitTopologyModified());
  }

  private deleteNode(node: any): void {
    this.topologyApiService
      .deleteNodeFromTopology(this.topologyData.topology.id, node.id())
      .subscribe(() => this.emitTopologyModified());
  }

  private copyNode(node: any): void {
    const topologyNode = this.findNodeById(node.id());
    this.openAddNodeDialog(topologyNode);
  }

  private addEdge(source: any, target: any): void {
    const sourceNode = this.findNodeById(source.id());
    const sourceNodeEdges = this.topologyData.topology.edges[source.id()];
    if (sourceNode.nodeType === TopologyNodeType.Branch) {
      let edgeName$: Observable<string> = EMPTY;
      if (sourceNodeEdges.length === 0) {
        edgeName$ = this.dialog.open(SelectEdgeDialogComponent, {data: Object.values(BranchOutput)})
          .afterClosed()
          .pipe(notNull(), pluck("edgeName"));
      } else if (sourceNodeEdges.length === 1) {
        edgeName$ = of(sourceNodeEdges[0].name === BranchOutput.MATCHED && BranchOutput.UNMATCHED || BranchOutput.MATCHED);
      }

      edgeName$
        .pipe(switchMap(edgeName => this.topologyApiService
          .addEdgeToTopology(this.topologyData.topology.id, source.id(), target.id(), false, edgeName)))
        .subscribe(() => this.emitTopologyModified());
    } else {
      this.topologyApiService
        .addEdgeToTopology(this.topologyData.topology.id, source.id(), target.id(), false)
        .subscribe(() => this.emitTopologyModified());
    }
  }

  private deleteEdge(edge: any): void {
    this.topologyApiService
      .deleteEdge(this.topologyData.topology.id, edge.source().id(), edge.target().id())
      .subscribe(() => this.emitTopologyModified());
  }

  private isEdgeValid(source: any, target: any): boolean {
    const targetId = target.id();
    const nodes = this.topologyData.topologyNodes;
    // root nodes cannot have incoming edges
    return nodes.find(node => node.id === targetId)?.nodeType !== TopologyNodeType.Root;
  }

  private isEdgeSourceValid(source: any): boolean {
    const sourceId = source.id();
    const sourceNode = this.findNodeById(sourceId);
    const sourceOutDegree = this.topologyData.topology.edges[sourceId].length;

    switch (sourceNode.nodeType) {
      case TopologyNodeType.Sink: // sink nodes cannot have outgoing edges
        return false;
      case TopologyNodeType.Branch:  // branch node can have 2 outgoing edges
        return sourceOutDegree < 2;
      default: // other nodes can have only 1 outgoing edge
        return sourceOutDegree < 1;
    }
  }

  private emitTopologyModified(): void {
    this.topologyModified.emit();
  }

  private findNodeById(nodeId: TopologyNodeId): TopologyNode | undefined {
    return this.topologyData?.topologyNodes.find(tn => tn.id === nodeId);
  }

  @HostListener("window:resize")
  private resetGraphLayout(): void {
    this.cyCore?.nodes().layout(this.layoutOptions).run();
  }
}
