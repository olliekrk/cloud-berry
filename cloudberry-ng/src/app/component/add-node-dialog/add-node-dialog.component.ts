import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
import {TopologyNodeType} from "../../model";

@Component({
  selector: "app-add-node-dialog",
  templateUrl: "./add-node-dialog.component.html",
  styleUrls: ["./add-node-dialog.component.scss"]
})
export class AddNodeDialogComponent implements OnInit {
  readonly TopologyNodeType = TopologyNodeType;
  form: FormGroup;

  constructor(formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      nodeType: [TopologyNodeType.Root],
      json: [],
    });
  }

  ngOnInit(): void {
  }

}
