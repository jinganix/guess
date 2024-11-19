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
import { CacheHolder } from "@modules/cache/cache.types";
import { components } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { MomentDetailScript } from "@pages/moment/detail/script";
import { MomentListScript } from "@pages/moment/list/script";
import { find } from "lodash";

export class MomentCache implements CacheHolder<Moment> {
  constructor() {}

  dispose(): void {}

  fetchData(): Promise<void> {
    return Promise.resolve();
  }

  async initialize(): Promise<Dispose[]> {
    return Promise.resolve([() => this.dispose()]);
  }

  private findInStore(id: string): Moment | undefined {
    for (const script of components.getAll(MomentDetailScript)) {
      if (script.moment?.id === id) {
        return script.moment;
      }
    }
    for (const script of components.getAll(MomentListScript)) {
      const moment = find(script.moments, (x) => x.id === id);
      if (moment) {
        return moment;
      }
    }
    return undefined;
  }

  getOrCreate(id: string): Moment {
    const moment = this.findInStore(id);
    return moment ? moment : new Moment();
  }

  get(id: string): Moment {
    const moment = this.findInStore(id);
    if (!moment) {
      throw new Error(`Uncached moment id: ${id}`);
    }
    return moment;
  }

  set(id: string, item: Moment): void {
    const moment = this.findInStore(id);
    moment?.copy(item);
  }

  remove(momentId: string): void {
    components.getAll(MomentDetailScript).forEach((x) => x.onDeleted(momentId));
    components.getAll(MomentListScript).forEach((x) => x.remove(momentId));
  }
}
