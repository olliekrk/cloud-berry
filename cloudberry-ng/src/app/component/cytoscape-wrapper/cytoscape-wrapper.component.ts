import {Component, ElementRef, Input, OnInit, ViewChild} from "@angular/core";
import * as cytoscape from "cytoscape";
import {Topology, TopologyNode} from "../../model";

@Component({
  selector: "app-cytoscape-wrapper",
  template: "<div #cy class=cy-container></div>",
  styleUrls: ["./cytoscape-wrapper.component.scss"]
})
export class CytoscapeWrapperComponent implements OnInit {

  @ViewChild("cy", {static: true}) cyContainer: ElementRef;

  @Input() topology: Topology;
  @Input() topologyNodes: TopologyNode[];

  cyCore?: cytoscape.Core;

  ngOnInit(): void {
    console.log(this.topology);
    console.log(this.topologyNodes);
    if (this.cyContainer) {
      this.cyCore = cytoscape({
        container: this.cyContainer.nativeElement,
        elements: [ // list of graph elements to start with
          { // node a
            data: {id: "a"}
          },
          { // node b
            data: {id: "b"}
          },
          { // edge ab
            data: {id: "ab", source: "a", target: "b"}
          }
        ],
        style: [ // the stylesheet for the graph
          {
            selector: "node",
            style: {
              "background-color": "#666",
              label: "data(id)"
            }
          },

          {
            selector: "edge",
            style: {
              width: 3,
              "line-color": "#ccc",
              "target-arrow-color": "#ccc",
              "target-arrow-shape": "triangle",
              "curve-style": "bezier"
            }
          }
        ],
      });
    }
  }

}
