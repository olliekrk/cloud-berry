import {Component, OnInit} from "@angular/core";
import {Topology, TopologyData} from "../../model";
import {Observable} from "rxjs";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";
import {TopologyNodeApiService} from "../../service/topology-node-api.service";
import {map, switchMap} from "rxjs/operators";


@Component({
  selector: "app-topology-dashboard",
  templateUrl: "./topology-dashboard.component.html",
  styleUrls: ["./topology-dashboard.component.scss"]
})
export class TopologyDashboardComponent implements OnInit {

  readonly activeTopology$: Observable<Topology>;
  readonly activeTopologyData$: Observable<TopologyData>;

  constructor(private activeTopologyStoreService: ActiveTopologyStoreService,
              private topologyNodeApiService: TopologyNodeApiService) {
    this.activeTopology$ = this.activeTopologyStoreService.stateUpdates();
    this.activeTopologyData$ = this.activeTopology$.pipe(switchMap(topology =>
      this.topologyNodeApiService.getTopologyNodes(topology.id).pipe(map(topologyNodes => ({topology, topologyNodes}))
      )));
  }

  ngOnInit(): void {
  }


}
