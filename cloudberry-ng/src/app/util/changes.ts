import {SimpleChange} from "@angular/core";

export type TypedSimpleChange<T> = SimpleChange & { currentValue: T };
