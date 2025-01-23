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

import { classId } from "@helpers/utils/utils";
import { CacheKey } from "@modules/cache/cache.service";
import { CacheItem } from "@modules/cache/cache.types";
import { cacheService } from "@modules/container";
import { IUserPb } from "@proto/UserProto";
import { makeAutoObservable } from "mobx";

function randName(id: string): string {
  const name = "游客" + Number(id.substring(8)).toString(32);
  return name.substring(name.length - 4);
}

export class User implements CacheItem {
  static readonly CLASS_ID = classId();
  cacheKey = "";
  id = "";
  avatar = "";
  gender = 0;
  nickname = "";

  constructor() {
    makeAutoObservable(this);
  }

  toKey(): CacheKey {
    return CacheKey.fromType(User, this.id);
  }

  dispose(): void {
    this.cacheKey = "";
    this.id = "";
    this.avatar = "";
    this.gender = 0;
    this.nickname = "";
  }

  static fromPb(pb: IUserPb): User {
    const x = new User().update(pb);
    cacheService.set(x);
    return x;
  }

  update(pb: IUserPb): User {
    this.id = pb.id ?? "";
    this.cacheKey = this.toKey().toString();
    this.avatar = pb.avatar ?? "";
    this.gender = pb.gender ?? 0;
    this.nickname = pb.nickname || "游客" + randName(pb.id);
    return this;
  }
}
