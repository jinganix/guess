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
import { ScriptedComponent, ScriptedPage } from "@helpers/wx/adapter.types";
import { Observer } from "@helpers/wx/observer";
import { pickObservable } from "@helpers/wx/pick.obervable";
import { components } from "@modules/container";
import { omit } from "lodash";

export abstract class ComponentScript {
  private static COUNTER = 1;
  private _disposes: Record<string, Dispose[]> = {};
  _scriptId = 1;
  _comp: ScriptedPage | ScriptedComponent;
  _observer: Observer;
  _picks: string[] = [];

  protected constructor(comp: ScriptedPage | ScriptedComponent, picks: string[]) {
    this._scriptId = ComponentScript.COUNTER++;
    this._comp = comp;
    this._picks = picks;
    this._observer = new Observer((path: string, value: unknown) => {
      this._comp.setData({ [path]: value });
    }, this._picks);
  }

  onPropertyChange(key: string, value: unknown): void {
    const obj = this as Record<string, unknown>;
    obj[key] = value;
  }

  abstract classId(): string;

  defaultData(excludes?: string[]): Record<string, unknown> {
    const data = pickObservable(this, this._picks);
    return excludes && excludes.length ? omit(data, excludes) : data;
  }

  observe(): void {
    this._observer.observe(this);
  }

  didMount(_query?: Record<string, string | undefined>): void {
    components.set(this);
  }

  willUnmount(): void {
    components.remove(this);
    Object.values(this._disposes).map((v) => v.map((x) => x()));
    this._disposes = {};
    this._observer.dispose();
  }

  get scriptId(): number {
    return this._scriptId;
  }

  get pageId(): string {
    return this._comp.getPageId();
  }
}
