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
import { IConfigPb, UserConfigRequest, UserConfigResponse } from "@proto/UserProto";
import { makeAutoObservable } from "mobx";

export class ConfigStore implements ModuleInitializer {
  private replay: Replay<void> = new Replay<void>();
  adBannerMe = "";
  adCustomMe = "";
  adCustomPuzzleRanking = "";
  adCustomPuzzleReply = "";
  adCustomMomentDetail = "";
  adCustomMomentList = "";
  adInterstitialScreen = "";
  adRewardedAnswerHint = "";
  adRewardedAnswerMoment = "";
  adRewardedPuzzleLimit = "";
  adVideoMe = "";
  adVideoPuzzleRanking = "";
  adVideoPuzzleReply = "";
  adVideoMomentDetail = "";
  adVideoMomentList = "";
  preview = false;
  puzzleDailyLimit = 30;
  puzzleLimitIncrease = 20;
  staticUrl = "";

  constructor() {
    makeAutoObservable(this);
    this.dispose();
  }

  dispose(): void {
    this.replay = new Replay<void>();
    this.adBannerMe = "adunit-9d07e0b3dbfd93b3";
    this.adCustomMe = "adunit-fa890690a6ea6e7b";
    this.adCustomPuzzleRanking = "adunit-67cd9e3eccb56f1e";
    this.adCustomPuzzleReply = "adunit-8550dc191326e562";
    this.adCustomMomentDetail = "adunit-f8c9168a0b1246a4";
    this.adCustomMomentList = "adunit-b1ba879b8de950c8";
    this.adInterstitialScreen = "adunit-f5da8c28bdf82480";
    this.adRewardedAnswerHint = "adunit-ba2b66660da33327";
    this.adRewardedAnswerMoment = "adunit-0199d86ac316ce45";
    this.adRewardedPuzzleLimit = "adunit-af856a82aa5ea0c6";
    this.adVideoMe = "adunit-0d2d4a957a431b71";
    this.adVideoPuzzleRanking = "adunit-a1d5c2a8bbfa60d7";
    this.adVideoPuzzleReply = "adunit-984208243743287c";
    this.adVideoMomentDetail = "adunit-a3351ca00e6b7686";
    this.adVideoMomentList = "adunit-dd1b54568771dde3";
    this.preview = false;
    this.puzzleDailyLimit = 30;
    this.puzzleLimitIncrease = 20;
    this.staticUrl = "";
  }

  async initialize(): Promise<Dispose[]> {
    await this.fetchData();
    return [() => this.dispose()];
  }

  async fetchData(force = false): Promise<void> {
    force && (this.replay = new Replay<void>());
    await this.replay.resolve(async () => {
      const res = await httpService.request(UserConfigRequest.create(), UserConfigResponse);
      res && this.update(res.config);
    });
  }

  updatePreview(preview: boolean): void {
    this.preview = preview;
  }

  update(pb: IConfigPb): void {
    Object.assign(this, pb);
  }
}
