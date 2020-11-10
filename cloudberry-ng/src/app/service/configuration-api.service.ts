import {Injectable} from "@angular/core";
import {ConfigurationRestService} from "../rest/configuration-rest.service";
import {Observable} from "rxjs";
import {ApiProperty} from "../model";
import {share} from "rxjs/operators";

@Injectable({
  providedIn: "root"
})
export class ConfigurationApiService {

  constructor(private rest: ConfigurationRestService) {
  }

  getAllProperties(): Observable<ApiProperty[]> {
    return this.rest.getAllProperties().pipe(share());
  }
}
