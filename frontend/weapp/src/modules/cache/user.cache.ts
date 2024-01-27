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

import { UserListRequest, UserListResponse } from "@proto/UserProto";
import { includes } from "lodash";
import { User } from "@modules/user/user.types";
import { httpService } from "@helpers/service/http.service";
import { LruCache } from "@modules/cache/lru.cache";

class DelayLoader {
  ids: string[] = [];

  constructor(load: (ids: string[]) => void) {
    setTimeout(() => load(this.ids), 0);
  }

  push(id: string): void {
    if (!includes(this.ids, id)) {
      this.ids.push(id);
    }
  }
}

export class UserCache extends LruCache<User> {
  private delayLoader: DelayLoader | null = null;

  constructor() {
    super();
  }

  getOrCreate(id: string): User {
    let user = this.get(id);
    if (!user) {
      user = new User();
      this.cache.set(id, user);
    }
    return user;
  }

  get(id: string): User {
    let user = this.cache.get(id);
    if (!user) {
      user = new User();
      this.cache.set(id, user);
      if (!this.delayLoader) {
        this.delayLoader = new DelayLoader((ids) => {
          void this.loadUsers(ids);
          this.delayLoader = null;
        });
      }
      this.delayLoader.push(id);
    }
    return user;
  }

  private async loadUsers(ids: string[]): Promise<void> {
    const userIds = ids.filter((id) => {
      if (!id) {
        return false;
      }
      if (this.cache.getRemainingTTL(id) < 60 * 1000) {
        return true;
      }
      const user = this.cache.get(id);
      return !user || !user.id;
    });
    if (userIds.length > 0) {
      const res = await httpService.request(
        UserListRequest.create({ ids: userIds }),
        UserListResponse,
      );
      if (res) {
        res.users.forEach((pb) => {
          const user = this.cache.get(pb.id);
          if (!user) {
            this.cache.set(pb.id, User.fromPb(pb));
          } else {
            this.cache.set(pb.id, user.update(pb));
          }
        });
      }
    }
  }
}
