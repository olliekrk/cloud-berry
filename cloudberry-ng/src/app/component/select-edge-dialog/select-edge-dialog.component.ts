import {Component, Inject, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: "app-select-edge-dialog",
  templateUrl: "./select-edge-dialog.component.html",
  styleUrls: ["./select-edge-dialog.component.scss"]
})
export class SelectEdgeDialogComponent implements OnInit {

  form: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public availableEdges: string[],
              formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      edgeName: [undefined, Validators.required],
    });
  }

  ngOnInit(): void {
  }

}
