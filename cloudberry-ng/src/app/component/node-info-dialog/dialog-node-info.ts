import {Component, Inject} from "@angular/core";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TopologyNode} from "../../model";

export interface NodeInfoDialogData {
  topologyNode: TopologyNode;
  rawString: String
}

@Component({
  selector: "dialog-content-example-dialog",
  templateUrl: "dialog-node-info.html",
})
export class DialogContentExampleDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public nodeInfoDialogData: NodeInfoDialogData) {
  }
}
