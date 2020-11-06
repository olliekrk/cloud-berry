import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {NGXLogger} from "ngx-logger";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";
import {map} from "rxjs/operators";

@Component({
  selector: "app-navigation-toolbar",
  templateUrl: "./navigation-toolbar.component.html",
  styleUrls: ["./navigation-toolbar.component.scss"]
})
export class NavigationToolbarComponent implements OnInit {

  readonly activeTopology = this.activeTopologyStoreService
    .stateUpdates()
    .pipe(map(t => `${t.name} (${t.id})`));

  constructor(private router: Router,
              private logger: NGXLogger,
              private activeTopologyStoreService: ActiveTopologyStoreService) {
  }

  ngOnInit(): void {
  }

  goToDashboard(): void {
    this.router.navigateByUrl("/").catch(e => this.logger.error(e));
  }

  reloadActiveTopology(): void {
    this.activeTopologyStoreService.doUpdate();
  }
}
