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

import { ComponentScript } from "@helpers/wx/component.script";
import {
  BehaviorOption,
  CompData,
  CompDataOption,
  CompInstanceMethods,
  CompInstanceProperties,
  CompLifetimes,
  CompOptions,
  CompOtherOption,
  CustomOption,
  FullProperty,
  ICustomShareContent,
  ICustomTimelineContent,
  MethodOption,
  PageData,
  PageDataOption,
  PageInstanceMethods,
  PageInstanceProperties,
  PageLifetimes,
  PageOptions,
  PropertyOption,
  PropertyType,
} from "@helpers/wx/wx.types";
import { includes } from "lodash";
import { cast } from "@helpers/utils/utils";
import { appService } from "@modules/container";

type Method = (this: Script, ...args: unknown[]) => void;
type Methods = Record<string, Method> & Script;
type Script = { script: Methods };

export const COMPONENT_LIFETIMES = ["created", "attached", "ready", "moved", "detached", "error"];
export const PAGE_LIFETIMES = ["onLoad", "onUnload"];

function delegatedMethods<T extends ComponentScript>(
  obj: {
    prototype: T;
  },
  excludes: string[],
): MethodOption {
  const methods: Record<string, Method> = {};
  Object.getOwnPropertyNames(obj.prototype).forEach((x) => {
    if (x !== "constructor" && !includes(excludes, x)) {
      methods[x] = function callScript(this: Script, ...args: unknown[]): unknown {
        return this.script[x](...args);
      };
    }
  });
  return methods as unknown as MethodOption & T;
}

export function componentMethods<T extends ComponentScript>(
  obj: { prototype: T },
  properties?: PropertyOption,
): MethodOption {
  const methods = delegatedMethods(obj, COMPONENT_LIFETIMES);
  if (properties) {
    const data: Record<string, unknown> = {};
    for (const key in properties) {
      const property = properties[key] as FullProperty<PropertyType>;
      data[key] = property.value;
      const methodName = `observe${key.charAt(0).toUpperCase() + key.slice(1)}`;
      methods[methodName] = function (this: Script, newVal: unknown): unknown {
        return this.script.onPropertyChanged(key, newVal);
      };
    }
  }
  return methods;
}

export function pageMethods<T extends ComponentScript>(obj: { prototype: T }): MethodOption {
  return {
    onShareAppMessage(this: ScriptedPage): ICustomShareContent | void {
      return appService.share(this.route ?? "");
    },

    onShareTimeline(this: ScriptedPage): ICustomTimelineContent | void {
      return appService.shareTimeline(this.route ?? "");
    },

    ...delegatedMethods(obj, PAGE_LIFETIMES),
  };
}

interface ScriptOption<T extends ComponentScript> {
  script: T;
}

export interface ScriptedPage<TScript = ComponentScript>
  extends PageData<PageDataOption>,
    PageLifetimes,
    PageInstanceProperties,
    PageInstanceMethods<PageDataOption>,
    ScriptOption<TScript> {}

// eslint-disable-next-line max-len
// eslint-disable-next-line @typescript-eslint/no-unsafe-declaration-merging,@typescript-eslint/no-unused-vars
export class ScriptedPage<TScript extends ComponentScript> {}

interface Properties {
  properties: PropertyOption;
}

export interface ScriptedComponent<TScript = ComponentScript>
  extends CompData<CompDataOption>,
    CompLifetimes,
    CompOtherOption,
    CompInstanceProperties,
    CompInstanceMethods<CompDataOption>,
    Properties,
    ScriptOption<TScript> {}

// eslint-disable-next-line max-len
// eslint-disable-next-line @typescript-eslint/no-unsafe-declaration-merging,@typescript-eslint/no-unused-vars
export class ScriptedComponent<TScript extends ComponentScript> {}

export function componentLifetimes<T extends ComponentScript>(
  factory: (comp: ScriptedComponent<T>) => T,
  properties?: PropertyOption,
): CompLifetimes["lifetimes"] {
  return {
    attached(this: ScriptedComponent<T>) {
      this.script = factory(this);
      if (properties) {
        Object.keys(properties).map((x) =>
          this.script.onPropertyChanged(x, this.data ? this.data[x] : undefined),
        );
      }
      this.script.didMount();
    },

    detached(this: ScriptedComponent<T>) {
      this.script.willUnmount();
    },
  };
}

export function pageLifetimes<T extends ComponentScript>(
  factory: (page: ScriptedPage<T>) => T,
): Partial<PageLifetimes> {
  return {
    onLoad(this: ScriptedPage<T>, query?: Record<string, string | undefined>) {
      this.script = factory(this);
      this.script.didMount(query);
    },

    onUnload(this: ScriptedPage<T>) {
      this.script.willUnmount();
    },
  };
}

export function defaultComponent<T extends ComponentScript>(
  type: { prototype: T },
  factory: (comp: ScriptedComponent<T>) => T,
  properties?: PropertyOption,
): CompOptions<PageDataOption, PropertyOption, MethodOption, BehaviorOption> {
  return {
    data: factory(cast({})).initData(properties && Object.keys(properties)),

    lifetimes: componentLifetimes(factory, properties),

    methods: componentMethods(type, properties),

    options: {
      styleIsolation: "apply-shared",
    },

    properties,
  };
}

export function defaultPage<T extends ComponentScript>(
  type: { prototype: T },
  factory: (comp: ScriptedPage<T>) => T,
): PageOptions<PageDataOption, CustomOption> {
  return {
    data: factory(cast({})).initData(),

    ...pageLifetimes(factory),

    ...pageMethods(type),
  };
}
