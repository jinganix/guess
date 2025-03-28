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

import { PopoverTimelineScript } from "@comps/popover-timeline/script";
import { PuzzleHintScript } from "@comps/puzzle-hint/script";
import { PuzzleLimitScript } from "@comps/puzzle-limit/script";
import { Pages } from "@helpers/const";
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { Replay } from "@helpers/promise/replay";
import { httpService } from "@helpers/service/http.service";
import { classId, trimContent } from "@helpers/utils/utils";
import { ScriptedPage } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { InterstitialAd, TappedEvent } from "@helpers/wx/wx.types";
import { components, configStore } from "@modules/container";
import {
  IPuzzlePb,
  PuzzleAnswerRequest,
  PuzzleAnswerResponse,
  PuzzleRetrieveRequest,
  PuzzleRetrieveResponse,
} from "@proto/PuzzleProto";
import { observable } from "mobx";
import { PICKS } from "./pick";

export class PuzzleDetailScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  private _replay: Replay<void> = new Replay<void>();
  private _interstitialAd: InterstitialAd | null = null;
  @observable accessor loading = true;
  @observable accessor id = 0;
  @observable accessor content = "";
  @observable accessor images: string[] = [];
  @observable accessor options: string[] = [];
  @observable accessor limit = 0;
  @observable accessor configStore = configStore;

  constructor(comp: ScriptedPage<PuzzleDetailScript>) {
    super(comp, PICKS);
  }

  classId(): string {
    return PuzzleDetailScript.CLASS_ID;
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
