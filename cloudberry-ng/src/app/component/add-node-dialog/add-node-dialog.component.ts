import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {TopologyNodeType, topologyNodeTypeRequiredFields} from "../../model";
import {MatDialogRef} from "@angular/material/dialog";

export interface AddNodeDialogResult {
  nodeType: TopologyNodeType;
  json: Record<string, any>;
}

type AddNodeDialogForm = FormGroup & {
  controls: {
    nodeType: FormControl;
    json: FormControl;
  }
};

@Component({
  selector: "app-add-node-dialog",
  templateUrl: "./add-node-dialog.component.html",
  styleUrls: ["./add-node-dialog.component.scss"]
})
export class AddNodeDialogComponent implements OnInit {
  readonly TopologyNodeType = TopologyNodeType;
  readonly topologyNodeTypeRequiredFields = topologyNodeTypeRequiredFields;
  form: AddNodeDialogForm;

  constructor(public dialogRef: MatDialogRef<AddNodeDialogComponent>,
              formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      nodeType: [TopologyNodeType.Root],
      json: [undefined, [Validators.required, this.validateJson()]],
    }) as AddNodeDialogForm;
  }

  ngOnInit(): void {
  }

  validateJson(): ValidatorFn {
    return (jsonFC: FormControl) => {
      try {
        const json = JSON.parse(jsonFC.value);
        const jsonKeys = Object.keys(json);
        const requiredFields = topologyNodeTypeRequiredFields[this.form.controls.nodeType.value as TopologyNodeType];
        if (!requiredFields.every(requiredField => jsonKeys.includes(requiredField))) {
          return {invalidJson: true};
        }
      } catch (e) {
        return {invalidJson: true};
      }
      return null;
    };
  }

  submitForm(): void {
    if (this.form.valid) {
      const {nodeType, json} = this.form.value;
      this.dialogRef.close({
        nodeType,
        json: JSON.parse(json),
      });
    }
  }
}
