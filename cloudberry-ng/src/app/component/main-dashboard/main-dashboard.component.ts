import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {Topology, TopologyId} from "../../model";
import {TopologyApiService} from "../../service/topology-api.service";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";
import {switchMap} from "rxjs/operators";
import {MatDialog} from "@angular/material/dialog";
import {TopologyCreateDialogComponent} from "../topology-create-dialog/topology-create-dialog.component";
import {notNull} from "../../util";

@Component({
  selector: "app-main-dashboard",
  templateUrl: "./main-dashboard.component.html",
  styleUrls: ["./main-dashboard.component.scss"]
})
export class MainDashboardComponent implements OnInit {

  availableTopologies$: Observable<Topology[]>;
  activeTopology$: Observable<Topology>;

  constructor(private topologyApiService: TopologyApiService,
              private activeTopologyStoreService: ActiveTopologyStoreService,
              private dialog: MatDialog) {
    this.activeTopology$ = activeTopologyStoreService.stateUpdates();
    this.availableTopologies$ = this.fetchAvailableTopologies();
  }

  ngOnInit(): void {
  }

  useTopology(id: TopologyId): void {
    this.activeTopologyStoreService.setAsActive(id).subscribe();
  }

  deleteTopology(id: TopologyId): void {
    this.availableTopologies$ = this.topologyApiService.deleteTopology(id)
      .pipe(switchMap(() => this.fetchAvailableTopologies()));
  }

  private fetchAvailableTopologies(): Observable<Topology[]> {
    return this.topologyApiService.getAvailableTopologies();
  }

  openAddTopologyDialog(): void {
    this.dialog.open(TopologyCreateDialogComponent).afterClosed()
      .pipe(notNull(), switchMap(name => this.topologyApiService.createTopology(name)))
      .subscribe(() => {
        this.availableTopologies$ = this.fetchAvailableTopologies();
      });
  }
}
