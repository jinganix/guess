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

import { Deferred } from "@helpers/promise/deferred";

export class Replay<T> {
  private resolvedAt = 0;

  private deferred: Deferred<T> | null = null;

  private key = "";

  constructor(value?: T | null) {
    if (value !== undefined && value !== null) {
      this.deferred = new Deferred<T>();
      this.deferred.resolve(value);
      this.resolvedAt = Date.now();
    }
  }

  async reset(): Promise<void> {
    this.deferred && (await this.deferred.promise);
    this.resolvedAt = 0;
    this.deferred = null;
    this.key = "";
  }

  async resolve(defer: () => Promise<T>, key = ""): Promise<T> {
    if (!this.deferred || (this.key != key && this.resolved)) {
      this.key = key;
      const deferred = new Deferred<T>();
      this.deferred = deferred;
      this.resolvedAt = 0;
      try {
        const value = await defer();
        this.resolvedAt = Date.now();
        deferred.resolve(value);
      } catch (err) {
        this.deferred = null;
        deferred.reject(err);
        return deferred.promise;
      }
    }
    return this.deferred.promise;
  }

  async value(): Promise<T | null> {
    return !this.deferred ? null : this.deferred.promise;
  }

  get resolved(): boolean {
    return this.resolvedAt > 0;
  }
}
