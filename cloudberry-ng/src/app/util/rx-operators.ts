import {MonoTypeOperatorFunction} from "rxjs";
import {filter} from "rxjs/operators";

export function notNull<T>(): MonoTypeOperatorFunction<T> {
  return filter(v => v != null);
}

