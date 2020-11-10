import {Component, OnDestroy, OnInit} from "@angular/core";
import {Topology, TopologyData, TopologyId} from "../../model";
import {BehaviorSubject, forkJoin, Observable} from "rxjs";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";
import {TopologyNodeApiService} from "../../service/topology-node-api.service";
import {switchMap, take} from "rxjs/operators";
import {TopologyApiService} from "../../service/topology-api.service";
import {notNull} from "../../util";


@Component({
  selector: "app-topology-dashboard",
  templateUrl: "./topology-dashboard.component.html",
  styleUrls: ["./topology-dashboard.component.scss"]
})
export class TopologyDashboardComponent implements OnInit, OnDestroy {

  selectedTopologyId$: BehaviorSubject<TopologyId | null> = new BehaviorSubject(null);
  selectedTopologyData$: Observable<TopologyData>;
  availableTopologies: Topology[];

  constructor(private activeTopologyStoreService: ActiveTopologyStoreService,
              private topologyNodeApiService: TopologyNodeApiService,
              private topologyApiService: TopologyApiService) {
  }

  ngOnInit(): void {
    this.fetchAvailableTopologies();
    this.selectedTopologyData$ = this.selectedTopologyId$.asObservable()
      .pipe(
        notNull(),
        switchMap((topologyId: TopologyId) => forkJoin({
          topology: this.topologyApiService.getTopology(topologyId),
          topologyNodes: this.topologyNodeApiService.getTopologyNodes(topologyId),
        })),
      );

    this.activeTopologyStoreService.stateIdUpdates()
      .pipe(take(1))
      .subscribe(topologyId => this.updateSelectedTopology(topologyId));
  }

  ngOnDestroy(): void {
    this.selectedTopologyId$.complete();
    this.selectedTopologyId$.unsubscribe();
  }

  updateSelectedTopology(topologyId: TopologyId): void {
    this.selectedTopologyId$.next(topologyId);
  }

  fetchAvailableTopologies(): void {
    this.topologyApiService.getAvailableTopologies()
      .subscribe(topologies => this.availableTopologies = topologies);
  }

  reloadSelectedTopology(): void {
    this.selectedTopologyId$.next(this.selectedTopologyId$.value);
  }

}
