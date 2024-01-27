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

import { RewardedVideoAd } from "@helpers/wx/wx.types";
import { components, configStore } from "@modules/container";
import { emitter } from "@helpers/event/emitter";
import { httpService } from "@helpers/service/http.service";
import { PuzzleIncreaseLimitRequest, PuzzleIncreaseLimitResponse } from "@proto/PuzzleProto";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { ConfigStore } from "@modules/config/config.store";
import { ScriptedComponent } from "@helpers/wx/adapter";
import { PuzzleDetailScript } from "@pages/puzzle/detail/script";
import { classId } from "@helpers/utils/utils";

const CONNECTOR = new Connector({
  configStore: DataPiker.align<ConfigStore>(["puzzleDailyLimit", "puzzleLimitIncrease"]),
  store: DataPiker.spread<PuzzleLimitScript>(["show"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class PuzzleLimitScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  private _videoAd: RewardedVideoAd | null = null;
  show = false;

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return PuzzleLimitScript.CLASS_ID;
  }

  source(): Source {
    return { configStore, store: this };
  }

  willUnmount(): void {
    super.willUnmount();
    this._videoAd?.destroy();
    this._videoAd = null;
    this.show = false;
  }

  async open(): Promise<void> {
    this.show = true;
    if (wx.createRewardedVideoAd && !this._videoAd) {
      await configStore.fetchData();
      this._videoAd = wx.createRewardedVideoAd({
        adUnitId: configStore.adRewardedPuzzleLimit,
      });
      this._videoAd.onClose((status) => {
        if ((status && status.isEnded) || status === undefined) {
          void this.increaseLimit();
        } else {
          void emitter.emit("notifier", "info", "请观看完广告");
        }
      });
      this._videoAd.onError(() => {});
    }
  }

  tapClose(): void {
    this.show = false;
  }

  async tapVideoAd(): Promise<void> {
    if (this._videoAd) {
      try {
        await this._videoAd.show();
      } catch (_e) {
        try {
          await this._videoAd.load();
          await this._videoAd.show();
        } catch (_e) {
          void emitter.emit("notifier", "info", "广告显示失败");
        }
        void emitter.emit("notifier", "info", "广告显示失败");
      }
    }
  }

  async increaseLimit(): Promise<void> {
    const res = await httpService.request(
      PuzzleIncreaseLimitRequest.create(),
      PuzzleIncreaseLimitResponse,
    );
    if (res) {
      components.getInPage(PuzzleDetailScript, this.pageId).updateLimit(res.limit);
      this.show = false;
      void emitter.emit("notifier", "info", `成功补充${res.limit}点体力`);
    }
  }
}
