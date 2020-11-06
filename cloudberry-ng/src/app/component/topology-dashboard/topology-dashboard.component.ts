import {Component, OnInit} from "@angular/core";
import {Topology, TopologyNode} from "../../model";
import {Observable} from "rxjs";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";
import {TopologyNodeApiService} from "../../service/topology-node-api.service";
import {switchMap} from "rxjs/operators";


@Component({
  selector: "app-topology-dashboard",
  templateUrl: "./topology-dashboard.component.html",
  styleUrls: ["./topology-dashboard.component.scss"]
})
export class TopologyDashboardComponent implements OnInit {

  readonly activeTopology$: Observable<Topology>;
  readonly activeTopologyNodes$: Observable<TopologyNode[]>;

  constructor(private activeTopologyStoreService: ActiveTopologyStoreService,
              private topologyNodeApiService: TopologyNodeApiService) {
    this.activeTopology$ = this.activeTopologyStoreService.stateUpdates();
    this.activeTopologyNodes$ = this.activeTopology$.pipe(switchMap(t => this.topologyNodeApiService.getTopologyNodes(t.id)));
  }

  ngOnInit(): void {
  }



}
