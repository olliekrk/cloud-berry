import {Component, OnDestroy, OnInit} from "@angular/core";
import {Topology, TopologyData, TopologyId} from "../../model";
import {BehaviorSubject, Observable} from "rxjs";
import {ActiveTopologyStoreService} from "../../service/active-topology-store.service";
import {TopologyNodeApiService} from "../../service/topology-node-api.service";
import {map, switchMap, take} from "rxjs/operators";
import {TopologyApiService} from "../../service/topology-api.service";
import {notNull} from "../../util";


@Component({
  selector: "app-topology-dashboard",
  templateUrl: "./topology-dashboard.component.html",
  styleUrls: ["./topology-dashboard.component.scss"]
})
export class TopologyDashboardComponent implements OnInit, OnDestroy {

  selectedTopology$: BehaviorSubject<Topology | null> = new BehaviorSubject(null);
  selectedTopologyData$: Observable<TopologyData>;
  availableTopologies: Topology[];

  constructor(private activeTopologyStoreService: ActiveTopologyStoreService,
              private topologyNodeApiService: TopologyNodeApiService,
              private topologyApiService: TopologyApiService) {
  }

  ngOnInit(): void {
    this.topologyApiService.getAvailableTopologies()
      .subscribe(topologies => this.availableTopologies = topologies);

    this.activeTopologyStoreService.stateUpdates()
      .pipe(take(1))
      .subscribe(topology => this.selectedTopology$.next(topology));

    this.selectedTopologyData$ = this.selectedTopology$.asObservable()
      .pipe(
        notNull(),
        switchMap((topology: Topology) => this.topologyNodeApiService.getTopologyNodes(topology.id)
          .pipe(
            map(topologyNodes => ({topology, topologyNodes}))
          )
        )
      );
  }

  ngOnDestroy(): void {
    this.selectedTopology$.complete();
    this.selectedTopology$.unsubscribe();
  }

  updateSelectedTopology(topologyId: TopologyId): void {
    const newTopology = this.availableTopologies?.find(topology => topology.id === topologyId);
    this.selectedTopology$.next(newTopology);
  }

  reloadSelectedTopology(): void {

  }

}
