import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from "@angular/core";
import * as cytoscape from "cytoscape";
import {TopologyData} from "../../model";
import {TypedSimpleChange} from "../../util";

type ComponentSimpleChanges = SimpleChanges & {
  topologyData?: TypedSimpleChange<TopologyData>;
};

@Component({
  selector: "app-cytoscape-wrapper",
  template: "<div #cy class=cy-container></div>",
  styleUrls: ["./cytoscape-wrapper.component.scss"]
})
export class CytoscapeWrapperComponent implements OnInit, OnChanges {

  @ViewChild("cy", {static: true}) cyContainer: ElementRef;

  @Input() topologyData: TopologyData;

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

  ngOnChanges({topologyData}: ComponentSimpleChanges): void {
    if (topologyData?.currentValue) {
      this.fillWithData(topologyData.currentValue);
    }
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
    }
  }

}
