import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from "@angular/core";
import * as cytoscape from "cytoscape";
import {Topology, TopologyNode} from "../../model";
import {TypedSimpleChange} from "../../util";

type ComponentSimpleChanges = SimpleChanges & {
  topology?: TypedSimpleChange<Topology>;
  topologyNodes?: TypedSimpleChange<TopologyNode[]>;
};

@Component({
  selector: "app-cytoscape-wrapper",
  template: "<div #cy class=cy-container></div>",
  styleUrls: ["./cytoscape-wrapper.component.scss"]
})
export class CytoscapeWrapperComponent implements OnInit, OnChanges {

  @ViewChild("cy", {static: true}) cyContainer: ElementRef;

  @Input() topology: Topology;
  @Input() topologyNodes: TopologyNode[];

  cyCore?: cytoscape.Core;

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
              label: "data(name)"
            }
          },
          {
            selector: "edge",
            style: {
              width: 1,
              label: "data(id)",
              "target-arrow-shape": "triangle",
              "curve-style": "haystack"
            }
          }
        ],
      });
    }
  }

  ngOnChanges({topology, topologyNodes}: ComponentSimpleChanges): void {
    if (topologyNodes?.currentValue) {
      this.fillWithTopologyNodesData(topologyNodes.currentValue);
    }
    if (topology?.currentValue) {
      this.fillWithTopologyData(topology.currentValue);
    }
  }

  // fixme: merge @Inputs into one struct so that nodes are always added first, otherwise it throws errors
  //  (cannot create edge with missing node)

  private fillWithTopologyData(topology: Topology): void {
    console.log("adding edges");
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
      this.cyCore.edges().remove();
      this.cyCore.add(edges);
    }
  }

  private fillWithTopologyNodesData(topologyNodes: TopologyNode[]): void {
    console.log("adding nodes");
    const nodes: cytoscape.NodeDefinition[] = topologyNodes.map(node => ({
      data: {
        id: node.id,
        name: node.name,
      }
    }));

    if (this.cyCore) {
      this.cyCore.nodes().remove();
      this.cyCore.add(nodes);
    }
  }

}
