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

import { pick } from "lodash";
import { isObservableProp, observe } from "mobx";

export type Listener = (path: string, value: unknown) => void;

interface ListenerDispose {
  path: string;
  dispose: () => void;
}

export function getObservableKeys(obj: object, includes?: string[]): string[] {
  const keys = [
    ...Object.keys(Object.getOwnPropertyDescriptors(Object.getPrototypeOf(obj))),
    ...Object.keys(obj),
  ];
  return keys.filter((x) => {
    if (includes?.length && !includes.includes(x)) {
      return false;
    }
    if (Array.isArray(obj)) {
      return false;
    }
    return isObservableProp(obj, x);
  });
}

export class Observer {
  disposers: ListenerDispose[] = [];
  listener: Listener;
  picks: string[] = [];

  constructor(listener: Listener, picks: string[] = []) {
    this.listener = listener;
    this.picks = picks.map((x) => String(x));
  }

  observe<T extends object = object>(obj: T, path = "", keys?: string[], fire = false): void {
    type K = keyof T;
    (keys || getObservableKeys(obj)).forEach((k) => {
      const prop = k as K;
      if (isObservableProp(obj, prop)) {
        const target = path ? `${path}.${String(prop)}` : String(prop);
        const dispose = observe(
          obj,
          prop,
          ({ newValue }) => {
            // TODO: Refine the judgment to improve performance
            this.disposers.filter((x) => {
              if (x.path.startsWith(`${target}.`)) {
                x.dispose();
                return false;
              }
              return true;
            });
            // TODO: Handle array observation
            if (typeof newValue === "object" && !Array.isArray(newValue) && newValue !== null) {
              const includes = this.picks
                .filter((x) => x.startsWith(`${target}.`))
                .map((x) => x.substring(x.lastIndexOf(".") + 1));
              const keys = getObservableKeys(newValue, includes);
              this.observe(newValue, target, keys, false);
              this.listener(target, pick(newValue, keys));
            } else {
              this.listener(target, newValue);
            }
          },
          fire,
        );
        this.disposers.push({ dispose, path: target });
      }
    });
  }

  dispose(): void {
    this.disposers.forEach(({ dispose }) => dispose());
    this.disposers = [];
  }
}
