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

import { Pages } from "@helpers/const";
import { tryInitializeModules } from "@helpers/module/module.initializer";
import { httpService } from "@helpers/service/http.service";
import { classId, formatUrl } from "@helpers/utils/utils";
import { ScriptedPage } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { TappedEvent } from "@helpers/wx/wx.types";
import { CacheKey } from "@modules/cache/cache.service";
import { configStore } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import {
  IMomentDetailPb,
  MomentCategory,
  MomentListRequest,
  MomentListResponse,
  MomentStatus,
} from "@proto/MomentProto";
import { find } from "lodash";
import { observable } from "mobx";
import { PICKS } from "./pick";

const CATEGORIES = {
  [MomentCategory.ALL]: "动态",
  [MomentCategory.MINE]: "我的动态",
  [MomentCategory.FOLLOWED]: "我的关注",
};

export class MomentListScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor category = MomentCategory.ALL;
  @observable accessor loading = true;
  @observable accessor more = true;
  @observable accessor moments: Moment[] = [];
  @observable accessor cacheKeys: string[] = [];
  @observable accessor configStore = configStore;

  constructor(comp: ScriptedPage) {
    super(comp, PICKS);
  }

  classId(): string {
    return MomentListScript.CLASS_ID;
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

  append(items: Moment[], pbs: IMomentDetailPb[]): void {
    this.updateMoments([...items, ...pbs.map((x) => Moment.fromPb(x))]);
  }

  prepend(pb: IMomentDetailPb): void {
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
