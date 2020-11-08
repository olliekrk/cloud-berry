import {Component, OnInit} from "@angular/core";
import {ConfigurationApiService} from "../../service/configuration-api.service";
import {Observable} from "rxjs";
import {ApiProperty} from "../../model";

@Component({
  selector: "app-configuration-dashboard",
  templateUrl: "./configuration-dashboard.component.html",
  styleUrls: ["./configuration-dashboard.component.scss"]
})
export class ConfigurationDashboardComponent implements OnInit {

  readonly apiProperties$: Observable<ApiProperty[]>;

  constructor(private configurationApiService: ConfigurationApiService) {
    this.apiProperties$ = configurationApiService.getAllProperties();
  }

  ngOnInit(): void {
  }

}
