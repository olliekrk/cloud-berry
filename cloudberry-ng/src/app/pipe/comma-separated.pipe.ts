import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "commaSeparated"
})
export class CommaSeparatedPipe implements PipeTransform {

  transform<T extends string>(value: T[]): string {
    return value.join(", ");
  }

}
