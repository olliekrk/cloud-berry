import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Topology} from "../model";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: "root"
})
export class TopologyRestService {

  readonly baseUrl: string = `${environment.apiUrl}/topology`;

  constructor(private httpClient: HttpClient) {

  }

  getActiveTopology(): Observable<Topology | null> {
    return this.httpClient.get<Topology | null>(this.baseUrl + "/active");
  }

}
