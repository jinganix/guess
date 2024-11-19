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

import { CommentEditorScript } from "@comps/comment-editor/script";
import { CommentListScript } from "@comps/comment-list/script";
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { Replay } from "@helpers/promise/replay";
import { httpService } from "@helpers/service/http.service";
import { Dispose } from "@helpers/types/types";
import { classId } from "@helpers/utils/utils";
import { ScriptedPage } from "@helpers/wx/adapter";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { TappedEvent } from "@helpers/wx/wx.types";
import { ConfigStore } from "@modules/config/config.store";
import { cacheService, components, configStore } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { MomentRetrieveRequest, MomentRetrieveResponse } from "@proto/MomentProto";

const CONNECTOR = new Connector({
  configStore: DataPiker.align<ConfigStore>(["adCustomMomentDetail"]),
  moment: DataPiker.spread<Moment>(["cacheKey", "comment"]),
  store: DataPiker.spread<MomentDetailScript>(["loading"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class MomentDetailScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  replay: Replay<void> = new Replay<void>();
  loading = true;
  momentId = "";
  moment: Moment | null = null;

  constructor(comp: ScriptedPage) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return MomentDetailScript.CLASS_ID;
  }

  source(): Source {
    return {
      configStore,
      moment: this.moment || Moment.INSTANCE,
      store: this,
    };
  }

  connect(): Dispose[] {
    return [];
  }

  didMount({ momentId }: { momentId: string }): void {
    super.didMount();
    this.momentId = momentId;
    void this.fetchData(true);
  }

  willUnmount(): void {
    super.willUnmount();
    this.loading = false;
    this.momentId = "";
    this.moment = null;
  }

  async fetchData(force: boolean): Promise<void> {
    if (!(await tryInitializeModules())) {
      return;
    }
    force && (this.replay = new Replay<void>());
    await this.replay.resolve(async () => {
      try {
        this.moment = cacheService.get(Moment, this.momentId);
        return;
      } catch (_) {
        this.loading = true;
        const res = await httpService.request(
          MomentRetrieveRequest.create({ id: this.momentId }),
          MomentRetrieveResponse,
        );
        res && (this.moment = Moment.fromPb(res.moment));
      }
    });
    this.loading = false;
    this.addDisposes(super.connect(), "connect");
  }

  onDeleted(momentId: string): void {
    if (this.momentId === momentId) {
      void wx.navigateBack();
    }
  }

  async onPullDownRefresh(): Promise<void> {
    await this.fetchData(true);
    await components.getInPage(CommentListScript, this.pageId).fetchData(true);
    await wx.stopPullDownRefresh();
  }

  async onReachBottom(): Promise<void> {
    await components.getInPage(CommentListScript, this.pageId).fetchData(false);
  }

  async tapAnswer(ev: TappedEvent<{ answer: number }>): Promise<void> {
    const answer = ev.currentTarget.dataset.answer;
    await this.moment?.handleAnswer(answer);
  }

  tapComment(): void {
    components.getInPage(CommentEditorScript, this.pageId).open(this.momentId, "");
  }
}
