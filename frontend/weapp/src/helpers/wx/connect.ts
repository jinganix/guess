/*
 * Copyright (c) 2020 jinganix@gmail.com, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * https://github.com/jinganix/guess
 */

import { Dispose } from "@helpers/types/types";
import { IArrayDidChange, IObjectDidChange, IValueDidChange, observe } from "mobx";
import { cloneDeep, flatten, range } from "lodash";

export type SetData = (data: object) => void;

export function publicKeys(obj: object): string[] {
  return Object.keys(obj).filter((x) => !x.startsWith("_"));
}

export class DataPiker<T = object> {
  static ALL = [];

  prefix?: string;

  props: (keyof T)[];

  private constructor(props: (keyof T)[], prefix?: string) {
    this.prefix = prefix;
    this.props = props;
  }

  static of<T extends object>(props: (keyof T)[], prefix: string): DataPiker<T> {
    return new DataPiker<T>(props, prefix);
  }

  static align<T extends object>(props: (keyof T)[]): DataPiker<T> {
    return new DataPiker<T>(props);
  }

  static spread<T extends object>(props: (keyof T)[]): DataPiker<T> {
    return new DataPiker<T>(props, "");
  }

  isAllProps(): boolean {
    return this.props.length === 0;
  }

  transformValue<K extends keyof T>(this: void, _prop: K, value: T[K]): T[K] {
    return typeof value === "object" ? cloneDeep(value) : value;
  }

  pick(item: T): Partial<T> {
    const data = {} as Partial<T>;
    const props: (keyof T)[] = this.isAllProps()
      ? (publicKeys(item as object) as (keyof T)[])
      : this.props;
    for (const prop of props) {
      data[prop] = this.transformValue(prop, item[prop]);
    }
    return data;
  }
}

type DataPickers<T = object> = {
  [K in keyof T]: DataPiker<T[K]>;
};

type DataSource<T> = {
  [K in keyof T]: T[K];
};

export type SourceType<Type> = Type extends Connector<infer X> ? X : never;

export class Connector<Source = object> {
  private readonly _pickers: DataPickers<Source>;

  private _source?: Source;

  constructor(pickers: DataPickers<Source>) {
    this._pickers = pickers;
  }

  source(source: DataSource<Source>): Connector<Source> {
    this._source = source;
    return this;
  }

  pick<K extends keyof Source>(source?: Source): Record<string, unknown> {
    const src = source || this._source;
    if (!src) {
      throw new Error("No source");
    }
    let data: Record<string, unknown> = {};
    for (const [key, value] of Object.entries(src) as [K, Source[K]][]) {
      const picker = this._pickers[key];
      const pre = picker.prefix ?? String(key);
      data = pre ? { ...data, [pre]: picker.pick(value) } : { ...data, ...picker.pick(value) };
    }
    return data;
  }

  connect(setData: SetData, setImmediately?: boolean): Dispose[];
  connect(setData: SetData, source: DataSource<Source>, setImmediately?: boolean): Dispose[];
  connect<K extends keyof Source>(
    setData: SetData,
    source?: DataSource<Source> | boolean,
    setImmediately = true,
  ): Dispose[] {
    const src = source && typeof source === "object" ? source : this._source;
    setImmediately = typeof source === "boolean" ? source : setImmediately;
    if (!src) {
      throw new Error("No source");
    }
    const disposes = flatten(
      (Object.entries(src) as [K, Source[K]][]).map(([key, value]) => {
        const picker = this._pickers[key];
        const prefix = picker.prefix ?? String(key);
        if (Array.isArray(value)) {
          return this.connectArray(value, prefix, setData);
        } else if (picker.isAllProps()) {
          return this.connectObject(value, prefix, picker, setData);
        } else {
          return this.connectProps(value, prefix, picker, setData);
        }
      }),
    );
    setImmediately && setData(this.pick(src));
    return disposes;
  }

  // TODO: not fully implemented
  private connectArray(item: Array<unknown>, prefix: string, setData: SetData): Dispose[] {
    return [
      observe(item, (change: IArrayDidChange<unknown>) => {
        if (change.type === "update") {
          const { index, newValue } = change;
          const data: Record<string, unknown> = {};
          data[prefix ? `${prefix}[${index}]` : `[${index}]`] = newValue;
          setData(data);
        } else if (change.type === "splice") {
          const { index, added, addedCount, removed, removedCount } = change;
          const [values, count] = addedCount > 0 ? [added, addedCount] : [removed, removedCount];
          const data: Record<string, unknown> = {};
          range(index, index + count).forEach(
            (x, i) =>
              (data[prefix ? `${prefix}[${x}]` : `[${x}]`] = addedCount > 0 ? values[i] : {}),
          );
          setData(data);
        }
      }),
    ];
  }

  private connectObject<T extends Source[keyof Source]>(
    item: T,
    prefix: string,
    piker: DataPiker<T>,
    setData: SetData,
  ): Dispose[] {
    return [
      observe(item as object, (change: IObjectDidChange<T>) => {
        if (change.type === "update" || change.type === "add") {
          type K = keyof T;
          const { name, newValue } = change as unknown as { name: string; newValue: T[K] };
          if (name.startsWith("_")) {
            return;
          }
          const data: Record<string, unknown> = {};
          const target = prefix ? `${prefix}.${name}` : name;
          data[target] = piker.transformValue(name as K, newValue);
          setData(data);
        }
      }),
    ];
  }

  private connectProps<T extends Source[keyof Source]>(
    item: T,
    prefix: string,
    piker: DataPiker<T>,
    setData: SetData,
  ): Dispose[] {
    return piker.props.map((prop) => {
      type K = keyof T;
      return observe(item, prop, (change: IValueDidChange<T[K]>) => {
        const { newValue } = change;
        const data: Record<string, unknown> = {};
        const target = prefix ? `${prefix}.${String(prop)}` : String(prop);
        data[target] = piker.transformValue(prop, newValue);
        setData(data);
      });
    });
  }
}
