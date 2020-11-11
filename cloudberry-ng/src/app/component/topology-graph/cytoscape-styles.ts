import * as cytoscape from "cytoscape";

export const cyStylesheets: cytoscape.Stylesheet[] = [
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
];