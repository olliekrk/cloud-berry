import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

export interface AddCounterNodeDialogResult {
  name: string;
  metricName: string;
}

@Component({
  selector: "app-add-counter-node-dialog",
  templateUrl: "./add-counter-node-dialog.component.html",
  styleUrls: ["./add-counter-node-dialog.component.scss"]
})
export class AddCounterNodeDialogComponent implements OnInit {

  form: FormGroup;

  constructor(formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      name: [undefined, Validators.required],
      metricName: [undefined, Validators.required],
    });
  }

  ngOnInit(): void {
  }

}
