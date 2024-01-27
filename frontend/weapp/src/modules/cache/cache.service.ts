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

import { CacheHolder, CacheItem } from "@modules/cache/cache.types";

type ProtoType<T> = { prototype: T; CLASS_ID: string };

export class CacheKey {
  constructor(
    public type: string,
    public id: string,
  ) {}

  static fromType<T extends CacheItem>(type: ProtoType<T>, id: string): CacheKey {
    return new CacheKey(type.CLASS_ID, id);
  }

  static fromKey(key: string): CacheKey {
    const [type, id] = key.split(":");
    return new CacheKey(type, id);
  }

  toString(): string {
    return `${this.type}:${this.id}`;
  }
}

export class CacheService {
  holders: Record<string, CacheHolder<unknown>> = {};

  constructor() {}

  getOrCreate<T extends CacheItem>(type: ProtoType<T>, id: string): T {
    const key = CacheKey.fromType(type, id);
    const holder = this.holders[key.type] as CacheHolder<T>;
    return holder.getOrCreate(key.id);
  }

  get<T extends CacheItem>(type: ProtoType<T>, id: string): T {
    const key = CacheKey.fromType(type, id);
    const holder = this.holders[key.type] as CacheHolder<T>;
    return holder.get(key.id);
  }

  getByKey<T extends CacheItem>(cacheKey: string): T {
    const key = CacheKey.fromKey(cacheKey);
    const holder = this.holders[key.type] as CacheHolder<T>;
    return holder.get(key.id);
  }

  set(data: CacheItem): void {
    const key = data.toKey();
    const holder = this.holders[key.type];
    return holder.set(key.id, data);
  }

  remove<T extends CacheItem>(type: ProtoType<T>, id: string): void {
    const key = CacheKey.fromType(type, id);
    const holder = this.holders[key.type] as CacheHolder<T>;
    holder.remove(key.id);
  }
}
