import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: "app-topology-create-dialog",
  templateUrl: "./topology-create-dialog.component.html",
  styleUrls: ["./topology-create-dialog.component.scss"]
})
export class TopologyCreateDialogComponent implements OnInit {

  form: FormGroup;

  constructor(formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      name: [],
    });
  }

  ngOnInit(): void {
  }

}
