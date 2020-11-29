import * as cytoscape from "cytoscape";
import {TopologyNodeType} from "../../model";

export const nodeDefaultColor = "#666";
export const nodesColors: Partial<Record<TopologyNodeType, string>> = {
  [TopologyNodeType.Counter]: "#88c",
  [TopologyNodeType.Filter]: "#f71",
  [TopologyNodeType.Map]: "#fef",
  [TopologyNodeType.Merge]: "#ac3",
  [TopologyNodeType.Root]: "#8ef",
  [TopologyNodeType.Sink]: "#cf4",
};

export const cyStylesheets: cytoscape.Stylesheet[] = [
  {
    selector: "node",
    style: {
      label: "data(name)",
      "text-valign": "center",
      "text-halign": "center",
      "font-size": "12px",
      "background-color": "data(bgColor)",
    }
  },
  {
    selector: "edge",
    style: {
      width: 1,
      content: "data(name)",
      "font-size": 8,
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
];
