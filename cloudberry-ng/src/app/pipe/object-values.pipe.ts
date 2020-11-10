import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "objectValues"
})
export class ObjectValuesPipe implements PipeTransform {

  transform<V>(value: Record<any, V>): V[] {
    return Object.values(value);
  }

}
