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

type ProtoType<T> = { prototype: T; CLASS_ID: string };

export class Components {
  scripts: Record<string, ComponentScript[]> = {};

  get<T extends ComponentScript>(type: ProtoType<T>, pageId?: string): T {
    const scripts = this.scripts[type.CLASS_ID] || [];
    for (const script of scripts) {
      if (script.pageId === pageId || pageId === undefined) {
        return script as T;
      }
    }
    throw new Error(`Component ${type.CLASS_ID} not found`);
  }

  getInPage<T extends ComponentScript>(type: ProtoType<T>, pageId: string): T {
    const scripts = this.scripts[type.CLASS_ID] || [];
    for (const script of scripts) {
      if (script.pageId === pageId) {
        return script as T;
      }
    }
    throw new Error(`Component ${type.CLASS_ID} not found`);
  }

  findInPage<T extends ComponentScript>(type: ProtoType<T>, pageId: string): T | undefined {
    for (const script of this.getAll(type)) {
      if (script.pageId === pageId) {
        return script;
      }
    }
    return undefined;
  }

  getAllInPage<T extends ComponentScript>(type: ProtoType<T>, pageId: string): T[] {
    return this.getAll(type).filter((x) => x.pageId === pageId);
  }

  getAll<T extends ComponentScript>(type: ProtoType<T>): T[] {
    return (this.scripts[type.CLASS_ID] || []) as T[];
  }

  set(script: ComponentScript): void {
    const key = script.classId();
    this.scripts[key] = [...(this.scripts[key] || []), script];
  }

  remove(script: ComponentScript): void {
    const key = script.classId();
    const comps = this.scripts[key] || [];
    this.scripts[key] = comps.filter((x) => x.scriptId != script.scriptId);
  }
}
