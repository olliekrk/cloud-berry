import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {Topology, TopologyId} from "../../model";
import {TopologyApiService} from "../../service/topology-api.service";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";

@Component({
  selector: "app-main-dashboard",
  templateUrl: "./main-dashboard.component.html",
  styleUrls: ["./main-dashboard.component.scss"]
})
export class MainDashboardComponent implements OnInit {

  readonly availableTopologies$: Observable<Topology[]>;
  readonly activeTopology$: Observable<Topology>;

  constructor(private topologyApiService: TopologyApiService,
              private activeTopologyStoreService: ActiveTopologyStoreService) {
    this.availableTopologies$ = topologyApiService.getAvailableTopologies();
    this.activeTopology$ = activeTopologyStoreService.stateUpdates();
  }

  ngOnInit(): void {
  }

  useTopology(id: TopologyId): void {
    this.activeTopologyStoreService.setAsActive(id).subscribe();
  }

  deleteTopology(id: TopologyId): void {
    this.topologyApiService.deleteTopology(id).subscribe();
  }

}
