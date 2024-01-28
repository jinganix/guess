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
import {
  IPuzzlePb,
  PuzzleAnswerRequest,
  PuzzleAnswerResponse,
  PuzzleRetrieveRequest,
  PuzzleRetrieveResponse,
} from "@proto/PuzzleProto";
import { Replay } from "@helpers/promise/replay";
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { classId, trimContent } from "@helpers/utils/utils";
import { appService, components, configStore } from "@modules/container";
import {
  ICustomShareContent,
  ICustomTimelineContent,
  InterstitialAd,
  TappedEvent,
} from "@helpers/wx/wx.types";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Pages } from "@helpers/const";
import { PopoverTimelineScript } from "@comps/popover-timeline/script";
import { PuzzleLimitScript } from "@comps/puzzle-limit/script";
import { ScriptedPage } from "@helpers/wx/adapter";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { ConfigStore } from "@modules/config/config.store";
import { PuzzleHintScript } from "@comps/puzzle-hint/script";

const CONNECTOR = new Connector({
  configStore: DataPiker.align<ConfigStore>(["adCustomPuzzleReply", "preview"]),
  store: DataPiker.align<PuzzleDetailScript>(DataPiker.ALL),
});
interface Source extends SourceType<typeof CONNECTOR> {}

export class PuzzleDetailScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  private _replay: Replay<void> = new Replay<void>();
  private _interstitialAd: InterstitialAd | null = null;
  loading = true;
  id = 0;
  content = "";
  images: string[] = [];
  options: string[] = [];
  limit = 0;

  constructor(page: ScriptedPage<PuzzleDetailScript>) {
    super(page, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return PuzzleDetailScript.CLASS_ID;
  }

  source(): Source {
    return { configStore, store: this };
  }

  didMount(): void {
    super.didMount();
    void this.fetchData();
    void this.showAd();
  }

  willUnmount(): void {
    super.willUnmount();
    this._replay = new Replay<void>();
    this._interstitialAd?.destroy();
    this._interstitialAd = null;
    this.loading = true;
    this.id = 0;
    this.content = "";
    this.images = [];
    this.options = [];
    this.limit = 0;
  }

  onShareAppMessage(): ICustomShareContent | void {
    return appService.share((this._comp as ScriptedPage).route ?? "");
  }

  onShareTimeline(): ICustomTimelineContent | void {
    return appService.shareTimeline((this._comp as ScriptedPage).route ?? "");
  }

  showAd(): void {
    if (!wx.createInterstitialAd) {
      return;
    }
    this._interstitialAd = wx.createInterstitialAd({
      adUnitId: configStore.adInterstitialScreen,
    });
    if (this._interstitialAd) {
      this._interstitialAd.show().catch((err) => console.log(err));
    }
  }

  async fetchData(force = false): Promise<void> {
    if (!(await tryInitializeModules())) {
      return;
    }
    force && (this._replay = new Replay<void>());
    await this._replay.resolve(async () => {
      const res = await httpService.request(PuzzleRetrieveRequest.create(), PuzzleRetrieveResponse);
      if (res) {
        this.update(res.puzzle);
        this.loading = false;
      }
    });
  }

  update(pb: IPuzzlePb): void {
    this.id = pb.id;
    this.content = pb.content;
    this.images = pb.images ? pb.images.map((x) => `${configStore.staticUrl}${x}`) : [];
    this.options = pb.options;
    this.limit = pb.limit;
  }

  updateLimit(limit: number): void {
    this.limit = limit;
  }

  tapHint(): void {
    void components.getInPage(PuzzleHintScript, this._comp.getPageId()).open();
  }

  tapRanking(): void {
    void wx.navigateTo({ url: Pages.PUZZLE_RANKING });
  }

  tapTimeline(): void {
    components.getInPage(PopoverTimelineScript, this._comp.getPageId()).open();
  }

  async tapAnswer(ev: TappedEvent<{ item: string }>): Promise<void> {
    const content = trimContent(ev.currentTarget.dataset.item);
    if (content.length === 0) {
      void wx.showToast({ icon: "error", title: "请输入答案" });
      return;
    }
    if (!this.limit) {
      void components.getInPage(PuzzleLimitScript, this._comp.getPageId()).open();
      return;
    }
    const res = await httpService.request(
      PuzzleAnswerRequest.create({ answer: content }),
      PuzzleAnswerResponse,
    );
    if (res) {
      if (res.correct) {
        await this.fetchData(true);
      } else {
        void wx.showToast({ icon: "error", title: "回答错误" });
      }
    }
  }
}
