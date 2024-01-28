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

import { LRUCache } from "lru-cache";
import { Dispose } from "@helpers/types/types";
import { CacheHolder } from "@modules/cache/cache.types";

export abstract class LruCache<T extends object> implements CacheHolder<T> {
  protected cache: LRUCache<string, T>;

  protected constructor() {
    this.cache = new LRUCache<string, T>({
      allowStale: false,
      max: 500,
      maxSize: 500,
      sizeCalculation: () => 1,
      ttl: 1000 * 60 * 3,
      updateAgeOnGet: true,
      updateAgeOnHas: false,
    });
  }

  abstract getOrCreate(id: string): T;

  dispose(): void {
    this.cache.clear();
  }

  fetchData(): Promise<void> {
    return Promise.resolve();
  }

  async initialize(): Promise<Dispose[]> {
    return Promise.resolve([() => this.dispose()]);
  }

  get(id: string): T {
    const x = this.cache.get(id);
    if (!x) {
      throw new Error(`Uncached id: ${id}`);
    }
    return x;
  }

  set(id: string, item: T): void {
    const x = this.cache.get(id);
    x && Object.assign(x, item);
    this.cache.set(id, x ? x : item);
  }

  remove(id: string): void {
    this.cache.delete(id);
  }
}
