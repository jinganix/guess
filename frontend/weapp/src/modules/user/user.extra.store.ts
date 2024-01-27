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

import { makeAutoObservable } from "mobx";
import { httpService } from "@helpers/service/http.service";
import { Dispose, ModuleInitializer } from "@helpers/types/types";
import { Replay } from "@helpers/promise/replay";
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { IUserExtraPb, UserExtraRequest, UserExtraResponse } from "@proto/UserProto";

export class UserExtraStore implements ModuleInitializer {
  private replay: Replay<void> = new Replay();
  moment = 0;
  follow = 0;

  constructor() {
    makeAutoObservable(this);
  }

  dispose(): void {
    this.moment = 0;
    this.follow = 0;
  }

  initialize(): Promise<Dispose[]> {
    return Promise.resolve([() => this.dispose()]);
  }

  async fetchData(force = false): Promise<void> {
    if (!(await tryInitializeModules())) {
      return;
    }
    force && (this.replay = new Replay<void>());
    await this.replay.resolve(async () => {
      const res = await httpService.request(UserExtraRequest.create(), UserExtraResponse);
      res && this.update(res.extra);
    });
  }

  update(pb: IUserExtraPb): void {
    this.moment = Math.max(0, pb.moment);
    this.follow = Math.max(0, pb.follow);
  }

  updateMoment(moment: number): void {
    this.moment = Math.max(0, moment);
  }

  updateFollow(follow: number): void {
    this.follow = Math.max(0, follow);
  }
}
