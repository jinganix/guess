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

import { tryInitializeModules } from "@helpers/module/module.initializer";
import { Replay } from "@helpers/promise/replay";
import { httpService } from "@helpers/service/http.service";
import { classId } from "@helpers/utils/utils";
import { ScriptedPage } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { configStore } from "@modules/container";
import { Ranking } from "@modules/ranking/ranking.types";
import { IPuzzleRankPb, PuzzleRankingRequest, PuzzleRankingResponse } from "@proto/PuzzleProto";
import { observable } from "mobx";
import { PICKS } from "./pick";

export class PuzzleRankingScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  private replay: Replay<void> = new Replay();
  @observable accessor loading = true;
  @observable accessor rankings: Ranking[] = [];
  @observable accessor configStore = configStore;

  constructor(comp: ScriptedPage) {
    super(comp, PICKS);
  }

  classId(): string {
    return PuzzleRankingScript.CLASS_ID;
  }

  didMount(): void {
    super.didMount();
    void this.fetchData(true);
  }

  willUnmount(): void {
    super.willUnmount();
    this.loading = true;
    this.rankings = [];
  }

  async fetchData(force = false): Promise<void> {
    this.loading = true;
    if (!(await tryInitializeModules())) {
      return;
    }
    force && (this.replay = new Replay<void>());
    await this.replay.resolve(async () => {
      const res = await httpService.request(PuzzleRankingRequest.create(), PuzzleRankingResponse);
      res && this.update(res.ranking);
    });
    this.loading = false;
  }

  update(pbs: IPuzzleRankPb[]): void {
    this.rankings = pbs.map((x) => Ranking.fromPb(x));
  }
}
