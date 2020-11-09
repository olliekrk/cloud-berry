import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: "app-add-node-dialog",
  templateUrl: "./add-node-dialog.component.html",
  styleUrls: ["./add-node-dialog.component.scss"]
})
export class AddNodeDialogComponent implements OnInit {
  form: FormGroup;

  constructor(formBuilder: FormBuilder) {
    // todo: handle add existing node & create node
    this.form = formBuilder.group({
      name: [],
      json: [],
    });
  }

  ngOnInit(): void {
  }

}
