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

import { httpService } from "@helpers/service/http.service";
import { Ranking } from "@modules/ranking/ranking.types";
import { IPuzzleRankPb, PuzzleRankingRequest, PuzzleRankingResponse } from "@proto/PuzzleProto";
import { Replay } from "@helpers/promise/replay";
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { ConfigStore } from "@modules/config/config.store";
import { ScriptedPage } from "@helpers/wx/adapter";
import { appService, configStore } from "@modules/container";
import { classId } from "@helpers/utils/utils";
import { ICustomShareContent, ICustomTimelineContent } from "@helpers/wx/wx.types";

const CONNECTOR = new Connector({
  configStore: DataPiker.align<ConfigStore>(["adCustomPuzzleRanking", "preview"]),
  store: DataPiker.align<PuzzleRankingScript>(["loading", "rankings"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class PuzzleRankingScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  private replay: Replay<void> = new Replay();
  loading = true;
  rankings: Ranking[] = [];

  constructor(comp: ScriptedPage) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return PuzzleRankingScript.CLASS_ID;
  }

  source(): Source {
    return { configStore, store: this };
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

  onShareAppMessage(): ICustomShareContent | void {
    return appService.share((this._comp as ScriptedPage).route ?? "");
  }

  onShareTimeline(): ICustomTimelineContent | void {
    return appService.shareTimeline((this._comp as ScriptedPage).route ?? "");
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
