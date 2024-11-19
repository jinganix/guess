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

import { Replay } from "@helpers/promise/replay";
import { httpService } from "@helpers/service/http.service";
import { Dispose, ModuleInitializer } from "@helpers/types/types";
import { User } from "@modules/user/user.types";
import { ErrorCode } from "@proto/AppErrorProto";
import { IUserPb, UserCurrentRequest, UserCurrentResponse } from "@proto/UserProto";
import { makeAutoObservable } from "mobx";

export class UserStore implements ModuleInitializer {
  private replay: Replay<void> = new Replay();
  userId = "";

  constructor() {
    makeAutoObservable(this);
  }

  dispose(): void {
    this.replay = new Replay<void>();
    this.userId = "";
  }

  async initialize(): Promise<Dispose[]> {
    await this.fetchData();
    return [() => this.dispose()];
  }

  async fetchData(force = false): Promise<void> {
    force && (this.replay = new Replay<void>());
    await this.replay.resolve(async () => {
      const res = await httpService.request(UserCurrentRequest.create(), UserCurrentResponse);
      if (!res) {
        throw ErrorCode.FORCE_LOGOUT;
      } else {
        this.update(res.user);
      }
    });
  }

  update(pb: IUserPb): void {
    this.userId = pb.id;
    User.fromPb(pb);
  }
}
