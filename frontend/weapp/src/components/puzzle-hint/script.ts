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

import { emitter } from "@helpers/event/emitter";
import { httpService } from "@helpers/service/http.service";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { RewardedVideoAd } from "@helpers/wx/wx.types";
import { configStore } from "@modules/container";
import { PuzzleHintRequest, PuzzleHintResponse } from "@proto/PuzzleProto";

const CONNECTOR = new Connector({ store: DataPiker.spread<PuzzleHintScript>(["answer", "show"]) });

interface Source extends SourceType<typeof CONNECTOR> {}

export class PuzzleHintScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  private _videoAd: RewardedVideoAd | null = null;
  answer = "";
  show = false;

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return PuzzleHintScript.CLASS_ID;
  }

  source(): Source {
    return { store: this };
  }

  willUnmount(): void {
    super.willUnmount();
    this._videoAd?.destroy();
    this._videoAd = null;
    this.answer = "";
    this.show = false;
  }

  async loadHint(): Promise<void> {
    const res = await httpService.request(PuzzleHintRequest.create(), PuzzleHintResponse);
    res && (this.answer = res.answer);
  }

  async open(): Promise<void> {
    this.show = true;
    if (wx.createRewardedVideoAd && !this._videoAd) {
      await configStore.fetchData();
      this._videoAd = wx.createRewardedVideoAd({
        adUnitId: configStore.adRewardedAnswerHint,
      });
      this._videoAd.onClose((status) => {
        if ((status && status.isEnded) || status === undefined) {
          void this.loadHint();
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
}
