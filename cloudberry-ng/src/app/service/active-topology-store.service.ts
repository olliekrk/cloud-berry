import {Injectable} from "@angular/core";
import {Store} from "rxjs-observable-store";
import {Topology, TopologyId} from "../model";
import {TopologyApiService} from "./topology-api.service";
import {interval, Observable, Subject} from "rxjs";
import {distinctUntilKeyChanged, pluck, shareReplay, startWith, switchMap, tap} from "rxjs/operators";
import {notNull} from "../util";


/**
 * This service is only suitable for referring to constant properties of active topology - e.g. its `id` or `name`.
 * i.e. using `edges` field may cause unexpected behavior as they are not always updated.
 */
@Injectable({
  providedIn: "root"
})
export class ActiveTopologyStoreService extends Store<Topology | null> {

  readonly refreshIntervalMillis: number = 5 * 60 * 100; // 5 minutes
  readonly doUpdate$: Subject<void> = new Subject<void>();

  constructor(private topologyApiService: TopologyApiService) {
    super(null);
    this.doUpdate$.pipe(
      switchMap(__ => topologyApiService.getActiveTopology()),
      notNull(),
      distinctUntilKeyChanged("id")
    ).subscribe(t => this.setState(t));

    interval(this.refreshIntervalMillis)
      .pipe(startWith(0))
      .subscribe(__ => this.doUpdate$.next());
  }

  doUpdate(): void {
    this.doUpdate$.next();
  }

  setAsActive(topologyId: TopologyId): Observable<void> {
    return this.topologyApiService.useTopology(topologyId).pipe(tap(() => this.doUpdate()));
  }

  stateUpdates(): Observable<Topology> {
    return this.state$.pipe(notNull(), shareReplay(1));
  }

  stateIdUpdates(): Observable<TopologyId> {
    return this.stateUpdates().pipe(pluck("id"));
  }

}
