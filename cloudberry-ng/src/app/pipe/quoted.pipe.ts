import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "quoted"
})
export class QuotedPipe implements PipeTransform {

  transform(value: string[]): string[] {
    return value.map(v => `"${v}"`);
  }

}
