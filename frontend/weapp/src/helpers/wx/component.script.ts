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
import { ScriptedComponent, ScriptedPage } from "@helpers/wx/adapter";
import { Connector, publicKeys } from "@helpers/wx/connect";
import { components } from "@modules/container";
import { fromPairs, omit } from "lodash";
import { AnnotationsMap, makeObservable, observable } from "mobx";

export function makePublicObservable<T extends object>(obj: T): AnnotationsMap<T, never> {
  return makeObservable(
    obj,
    fromPairs(publicKeys(obj).map((x) => [x, observable])) as AnnotationsMap<T, never>,
  );
}

export abstract class ComponentScript<Source = object> {
  private static COUNTER = 1;
  private _disposes: Record<string, Dispose[]> = {};
  _scriptId = 1;
  _comp: ScriptedPage | ScriptedComponent;
  _connector?: Connector<Source>;

  protected constructor(comp: ScriptedPage | ScriptedComponent, connector?: Connector<Source>) {
    this._scriptId = ComponentScript.COUNTER++;
    this._comp = comp;
    this._connector = connector;
  }

  onPropertyChanged(key: string, value: unknown): void {
    if ((this as Record<string, unknown>)[key] !== value) {
      (this as Record<string, unknown>)[key] = value;
    }
  }

  addDisposes(disposes: Dispose[], key?: string): void {
    if (key) {
      const values = this._disposes[key] || [];
      values.forEach((x) => x());
      this._disposes[key] = disposes;
    } else {
      this._disposes[""] || (this._disposes[""] = []);
      this._disposes[""].push(...disposes);
    }
  }

  abstract classId(): string;

  abstract source(): Source;

  initData(excludes?: string[]): Record<string, unknown> {
    if (!this._connector) {
      return {};
    }
    const data = this._connector.pick(this.source());
    return excludes && excludes.length ? omit(data, excludes) : data;
  }

  connect(): Dispose[] {
    return this._connector?.connect((x) => this._comp.setData(x), this.source(), true) ?? [];
  }

  didMount(_query?: Record<string, string | undefined>): void {
    components.set(this as ComponentScript);
    this.addDisposes(this.connect());
  }

  willUnmount(): void {
    components.remove(this as ComponentScript);
    Object.values(this._disposes).map((v) => v.map((x) => x()));
    this._disposes = {};
  }

  get scriptId(): number {
    return this._scriptId;
  }

  get pageId(): string {
    return this._comp.getPageId();
  }
}
