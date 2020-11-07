import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from "@angular/core";
import * as cytoscape from "cytoscape";
import {TopologyData} from "../../model";
import {TypedSimpleChange} from "../../util";
import * as cxtmenu from "cytoscape-cxtmenu";

import dagre from "cytoscape-dagre";
import {MatDialog} from "@angular/material/dialog";
import {DialogContentExampleDialog} from "../node-info-dialog/dialog-node-info";

cytoscape.use(dagre);
cytoscape.use(cxtmenu);


type ComponentSimpleChanges = SimpleChanges & {
  topologyData?: TypedSimpleChange<TopologyData>;
};

@Component({
  selector: "app-cytoscape-wrapper",
  template: "<div #cy class=cy-container></div>",
  styleUrls: ["./cytoscape-wrapper.component.scss"]
})
export class CytoscapeWrapperComponent implements OnInit, OnChanges {

  constructor(public dialog: MatDialog) {
  }

  @ViewChild("cy", {static: true}) cyContainer: ElementRef;

  @Input() topologyData: TopologyData;

  cyCore?: cytoscape.Core;

  layout = {
    name: "dagre",
    rankDir: "LR"
  };

  ngOnInit(): void {
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
              // label: "data(id)",
              "line-color": "#e2004f",
              "target-arrow-shape": "triangle",
              "target-arrow-color": "#ff0000",
              "curve-style": "bezier"
            }
          }
        ],
        layout: this.layout
      });

    }
  }


  ngOnChanges({topologyData}: ComponentSimpleChanges): void {
    if (topologyData?.currentValue) {
      console.log(topologyData);
      this.fillWithData(topologyData.currentValue);
    }
    if (this.cyCore) {
      let matDialog = this.dialog;
      let data = this.topologyData;
      let defaults = this.getCxtMenuConfig(data, matDialog);
      // @ts-ignore
      this.cyCore.cxtmenu(defaults);
    }
  }

  private getCxtMenuConfig(topologyData: TopologyData, matDialog: MatDialog) {
    return {
      menuRadius: 100, // the radius of the circular menu in pixels
      selector: "node", // elements matching this Cytoscape.js selector will trigger cxtmenus
      commands: [
        {
          content: "View node details",
          select: function(node) {
            let foundNodeData = topologyData.topologyNodes.filter(function(fitoFilter) {
              return fitoFilter.id === node.id();
            })[0];
            const dialogRef = matDialog.open(DialogContentExampleDialog, {
              data: {
                topologyNode: foundNodeData,
                rawString: JSON.stringify(foundNodeData)
              }
            });
            dialogRef.afterClosed().subscribe(result => {
            });
          }
        },
        {
          content: "Edit node",
          select: function(node) {
            console.log(node.id());
          }
        },
        {
          content: "Remove node",
          select: function(node) {
            console.log(node.id());
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

      let lay = this.cyCore.nodes().layout(this.layout);
      lay.run();
    }
  }

}
