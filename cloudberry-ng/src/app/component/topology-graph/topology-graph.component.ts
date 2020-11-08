import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from "@angular/core";
import {TopologyData} from "../../model";
import {TypedSimpleChange} from "../../util";
import {MatDialog} from "@angular/material/dialog";
import {TopologyNodeDetailsInfoDialogComponent} from "../node-info-dialog/topology-node-details-info-dialog.component";
import * as cytoscape from "cytoscape";
import * as cxtmenu from "cytoscape-cxtmenu";
import dagre from "cytoscape-dagre";

cytoscape.use(dagre);
cytoscape.use(cxtmenu);

type ComponentSimpleChanges = SimpleChanges & {
  topologyData?: TypedSimpleChange<TopologyData>;
};

@Component({
  selector: "app-topology-graph",
  template: "<div #cy class=cy-container></div>",
  styleUrls: ["./topology-graph.component.scss"]
})
export class TopologyGraphComponent implements OnInit, OnChanges {

  @ViewChild("cy", {static: true}) cyContainer: ElementRef;

  @Input() topologyData: TopologyData;

  cyCore?: cytoscape.Core & any;

  readonly layoutOptions = {
    name: "dagre",
    rankDir: "LR"
  };

  constructor(public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.initializeCytoscape();
  }

  ngOnChanges({topologyData}: ComponentSimpleChanges): void {
    if (topologyData?.currentValue) {
      this.fillWithData(topologyData.currentValue);
    }
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
              "line-color": "#e2004f",
              "target-arrow-shape": "triangle",
              "target-arrow-color": "#ff0000",
              "curve-style": "unbundled-bezier"
            }
          }
        ],
        layout: this.layoutOptions
      });
      this.cyCore.cxtmenu(this.getCxtMenuConfig());
    }
  }

  private getCxtMenuConfig(): any {
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
          select: node => {
            console.log("Delete:", node.id());
          }
        },
      ],
      openMenuEvents: "cxttap", // cytoscape events that will open the menu (space separated)
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

}
