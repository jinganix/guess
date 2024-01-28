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
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { ConfigStore } from "@modules/config/config.store";
import { ScriptedPage } from "@helpers/wx/adapter";
import { configStore } from "@modules/container";
import {
  IMomentFacadePb,
  MomentCategory,
  MomentListRequest,
  MomentListResponse,
  MomentStatus,
} from "@proto/MomentProto";
import { Moment } from "@modules/moment/moment.types";
import { find } from "lodash";
import { CacheKey } from "@modules/cache/cache.service";
import { classId, formatUrl } from "@helpers/utils/utils";
import { Pages } from "@helpers/const";
import { TappedEvent } from "@helpers/wx/wx.types";

const CATEGORIES = {
  [MomentCategory.ALL]: "动态",
  [MomentCategory.MINE]: "我的动态",
  [MomentCategory.FOLLOWED]: "我的关注",
};

const CONNECTOR = new Connector({
  configStore: DataPiker.align<ConfigStore>(["adCustomMomentList"]),
  store: DataPiker.align<MomentListScript>(["loading", "moments", "more", "cacheKeys"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class MomentListScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  category = MomentCategory.ALL;
  loading = true;
  more = true;
  moments: Moment[] = [];
  cacheKeys: string[] = [];

  constructor(comp: ScriptedPage) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return MomentListScript.CLASS_ID;
  }

  source(): Source {
    return { configStore, store: this };
  }

  didMount({ category }: { category?: string }): void {
    super.didMount();
    this.category = category ? Number(category) : MomentCategory.ALL;
    void wx.setNavigationBarTitle({ title: CATEGORIES[this.category] });
    void this.fetchData(true);
  }

  willUnmount(): void {
    super.willUnmount();
    this.loading = true;
    this.more = true;
    this.moments = [];
    this.cacheKeys = [];
  }

  async onPullDownRefresh(): Promise<void> {
    await this.fetchData(true);
    await wx.stopPullDownRefresh();
  }

  async onReachBottom(): Promise<void> {
    await this.fetchData(false);
  }

  async fetchData(reset: boolean): Promise<void> {
    if (!this.more && !reset) {
      return;
    }
    this.loading = true;
    if (!(await tryInitializeModules())) {
      return;
    }
    const moments = reset ? [] : this.moments;
    const moment = moments[this.moments.length - 1];
    const res = await httpService.request(
      MomentListRequest.create({
        category: this.category,
        createdAt: moment?.createdAt ?? 0,
        id: moment?.id ?? "",
      }),
      MomentListResponse,
    );
    if (res) {
      this.more = res.more;
      this.append(moments, res.moments);
    }
    this.loading = false;
  }

  private updateMoments(moments: Moment[]): void {
    this.moments = moments;
    this.cacheKeys = this.moments.map((x) => x.cacheKey);
  }

  append(items: Moment[], pbs: IMomentFacadePb[]): void {
    this.updateMoments([...items, ...pbs.map((x) => Moment.fromPb(x))]);
  }

  prepend(pb: IMomentFacadePb): void {
    this.updateMoments([Moment.fromPb(pb), ...this.moments]);
  }

  find(momentId: string): Moment | null {
    return find(this.moments, (e) => e.id === momentId) ?? null;
  }

  remove(momentId: string): void {
    if (this.category === MomentCategory.FOLLOWED) {
      const moment = find(this.moments, (x) => x.id === momentId);
      moment && (moment.status = MomentStatus.DELETED);
    } else {
      this.updateMoments(this.moments.filter((e) => e.id !== momentId));
    }
  }

  tapEdit(): void {
    void wx.navigateTo({ url: Pages.MOMENT_EDIT });
  }

  tapMoment(ev: TappedEvent<{ item: string }>): void {
    const cacheKey = CacheKey.fromKey(ev.currentTarget.dataset.item);
    void wx.navigateTo({ url: formatUrl(Pages.MOMENT_DETAIL, { momentId: cacheKey.id }) });
  }
}
