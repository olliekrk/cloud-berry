import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ApiProperty} from "../model";

@Injectable({
  providedIn: "root"
})
export class ConfigurationRestService {

  readonly baseUrl: string = `${environment.apiUrl}/apiConfiguration`;

  constructor(private httpClient: HttpClient) {
  }

  getAllProperties(): Observable<ApiProperty[]> {
    return this.httpClient.get<ApiProperty[]>(`${this.baseUrl}/allProperties`);
  }
}
