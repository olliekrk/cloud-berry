import {Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from "@angular/core";
import {TopologyData, TopologyNode} from "../../model";
import {TypedSimpleChange} from "../../util";
import {MatDialog} from "@angular/material/dialog";
import {TopologyNodeDetailsInfoDialogComponent} from "../node-info-dialog/topology-node-details-info-dialog.component";
import * as cytoscape from "cytoscape";
import cxtmenu from "cytoscape-cxtmenu";
import dagre from "cytoscape-dagre";
import edgehandles from "cytoscape-edgehandles";
import {TopologyNodeApiService} from "../../service/topology-node-api.service";
import {TopologyApiService} from "../../service/topology-api.service";
import {AddNodeDialogComponent} from "../add-node-dialog/add-node-dialog.component";

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
    rankDir: "LR"
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
      this.fillWithData(topologyData.currentValue);
    }
  }

  openAddNodeDialog(): void {
    this.dialog.open(AddNodeDialogComponent, null);
  }

  private initializeCytoscape(): void {
    if (this.cyContainer) {
      this.cyCore = cytoscape({
        container: this.cyContainer.nativeElement,
        elements: {
          nodes: [],
          edges: [],
        },
        style: [
          {
            selector: "node",
            style: {
              label: "data(name)",
              "text-valign": "center",
              "text-halign": "center",
              "font-size": "12px"
            }
          },
          {
            selector: "edge",
            style: {
              width: 1,
              "line-color": "#ff0000",
              "target-arrow-shape": "triangle",
              "target-arrow-color": "#ff0000",
              "curve-style": "unbundled-bezier"
            }
          },
          {
            selector: ".eh-handle",
            style: {
              "background-color": "#0800ff",
              width: 12,
              height: 12,
              shape: "ellipse",
              "overlay-opacity": 0,
              "border-width": 12, // makes the handle easier to hit
              "border-opacity": 0
            }
          },
          {
            selector: ".eh-hover",
            style: {
              "background-color": "#0800ff"
            }
          },
          {
            selector: ".eh-source",
            style: {
              "border-width": 2,
              "border-color": "#0800ff"
            }
          },
          {
            selector: ".eh-target",
            style: {
              "border-width": 2,
              "border-color": "#0800ff"
            }
          },

          {
            selector: ".eh-preview, .eh-ghost-edge",
            style: {
              "line-color": "#0800ff",
              "target-arrow-color": "#0800ff",
              "source-arrow-color": "#0800ff"
            }
          },
          {
            selector: ".eh-ghost-edge.eh-preview-active",
            style: {
              opacity: 0
            }
          }
        ],
        layout: this.layoutOptions
      });
      this.cyCore.cxtmenu(this.getCxtMenuNodeConfig());
      this.cyCore.cxtmenu(this.getCxtMenuEdgeConfig());
      this.cyCore.edgehandles(this.getEdgeHandlesConfig());
    }
  }

  private getCxtMenuNodeConfig(): any {
    return {
      menuRadius: 80, // the radius of the circular menu in pixels
      selector: "node", // elements matching this Cytoscape.js selector will trigger cxtmenus
      commands: [
        {
          content: "Show details",
          select: node => {
            const topologyNode = this.topologyData?.topologyNodes.find(tn => tn.id === node.id());
            if (topologyNode) {
              this.dialog.open(TopologyNodeDetailsInfoDialogComponent, {data: {topologyNode}});
            }
          }
        },
        {
          content: "Edit",
          select: node => {
            console.log("Edit:", node.id());
          }
        },
        {
          content: "Delete",
          select: node => this.deleteNode(node),
        },
      ],
      openMenuEvents: "cxttap", // cytoscape events that will open the menu (space separated)
    };
  }

  private getCxtMenuEdgeConfig(): any {
    return {
      menuRadius: 80, // the radius of the circular menu in pixels
      selector: "edge", // elements matching this Cytoscape.js selector will trigger cxtmenus
      commands: [
        {
          content: "Add counter node",
          select: edge => this.createMockCounterNode(edge), // fixme
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
      handleNodes: node => { // whether node can be start of an edge
        return true;
      },
      snap: true,
      complete: (source, target, added) => { // after an edge is added
        this.cyCore.remove(`edge[id="${added.id()}"]`);
        console.log("new edge: ", source.id(), target.id());
      }
    };
  }

  private fillWithData({topology, topologyNodes}: TopologyData): void {
    const nodes: cytoscape.NodeDefinition[] = topologyNodes.map(node => ({
      data: {
        id: node.id,
        name: node.name,
      }
    }));

    const edgesArrays: cytoscape.EdgeDefinition[][] = Object.entries(topology.edges)
      .map(([source, targets]) => targets
        .map(target => ({
          data: {
            id: `${source}_${target}`,
            source,
            target
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
      this.cyCore.nodes().layout(this.layoutOptions).run();
    }
  }

  private createMockCounterNode(edge: any): void {
    this.topologyNodeApiService
      .addCounterNode("nowy", "metryka")
      .subscribe(newNode => {
        const topologyId = this.topologyData.topology.id;
        const sourceId = edge.source().id();
        const targetId = edge.target().id();
        this.topologyApiService
          .addNodeBetweenNodes(topologyId, sourceId, newNode.id, targetId, true)
          .subscribe(() => {
            this.createMockCounterNodeOnGraph(edge, newNode);
            this.topologyModified.emit();
          });
      });
  }

  private createMockCounterNodeOnGraph(edge: any, createdNode: TopologyNode): void {
    const sourceId = edge.source().id();
    const targetId = edge.target().id();
    const newNodeDefinition: cytoscape.NodeDefinition = {
      data: {
        id: createdNode.id,
        name: createdNode.name,
      }
    };
    const sourceToNewEdgeDefinition: cytoscape.EdgeDefinition = {
      data: {
        id: `${sourceId}_${createdNode.id}`,
        source: sourceId,
        target: createdNode.id,
      }
    };
    const newToTargetEdgeDefinition: cytoscape.EdgeDefinition = {
      data: {
        id: `${createdNode.id}_${targetId}`,
        source: createdNode.id,
        target: targetId,
      }
    };

    this.deleteEdgeOnGraph(edge);
    this.cyCore.add([newNodeDefinition, sourceToNewEdgeDefinition, newToTargetEdgeDefinition]);
  }

  private deleteEdge(edge: any): void {
    this.topologyApiService
      .deleteEdge(this.topologyData.topology.id, edge.source().id(), edge.target().id())
      .subscribe(() => this.deleteEdgeOnGraph(edge));
  }

  private deleteEdgeOnGraph(edge: any): void {
    this.cyCore.remove(`edge[id="${edge.id()}"]`);
  }

  private deleteNode(node: any): void {
    this.topologyApiService
      .deleteNodeFromTopology(this.topologyData.topology.id, node.id())
      .subscribe(() => this.deleteNodeOnGraph(node));
  }

  private deleteNodeOnGraph(node: any): void {
    this.cyCore.remove(`node[id="${node.id()}"]`);
  }
}
