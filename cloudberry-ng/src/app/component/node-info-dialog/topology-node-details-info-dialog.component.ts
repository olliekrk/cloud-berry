import {Component, Inject} from "@angular/core";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TopologyNode} from "../../model";

interface DialogData {
  topologyNode: TopologyNode;
}

@Component({
  selector: "app-topology-node-details-info-dialog",
  styleUrls: ["./topology-node-details-info-dialog.scss"],
  templateUrl: "./topology-node-details-info-dialog.html",
})
export class TopologyNodeDetailsInfoDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }
}
