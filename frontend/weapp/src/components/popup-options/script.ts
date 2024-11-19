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

import { Dispose } from "@helpers/types/types";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { CacheKey } from "@modules/cache/cache.service";
import { Comment } from "@modules/comment/comment.types";
import { cacheService, userStore } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { UserStore } from "@modules/user/user.store";

const CONNECTOR = new Connector({
  moment: DataPiker.spread<Moment>(["userId", "reported"]),
  store: DataPiker.spread<PopupOptionsScript>(["show"]),
  userStore: DataPiker.align<UserStore>(["userId"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class PopupOptionsScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  cacheKey = "";
  show = false;

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return PopupOptionsScript.CLASS_ID;
  }

  source(): Source {
    return {
      moment: (this.cacheKey && cacheService.getByKey(this.cacheKey)) || Moment.INSTANCE,
      store: this,
      userStore,
    };
  }

  connect(): Dispose[] {
    return [];
  }

  willUnmount(): void {
    super.willUnmount();
    this.cacheKey = "";
    this.show = false;
  }

  open(cacheKey: string): void {
    this.cacheKey = cacheKey;
    this.show = true;
    this.addDisposes(super.connect(), "connect");
  }

  onClose(): void {
    this.cacheKey = "";
    this.show = false;
  }

  async tapDelete(): Promise<void> {
    const key = CacheKey.fromKey(this.cacheKey);
    if (key.type === Moment.CLASS_ID) {
      const moment: Moment = cacheService.getByKey(this.cacheKey);
      if (await moment.delete()) {
        this.show = false;
      }
    } else {
      const comment: Comment = cacheService.getByKey(this.cacheKey);
      if (await comment.delete()) {
        this.show = false;
      }
    }
  }

  async tapReport(): Promise<void> {
    const key = CacheKey.fromKey(this.cacheKey);
    if (key.type === Moment.CLASS_ID) {
      const moment: Moment = cacheService.getByKey(this.cacheKey);
      if (await moment.toggleReport()) {
        this.show = false;
      }
    } else {
      const comment: Comment = cacheService.getByKey(this.cacheKey);
      if (await comment.toggleReport()) {
        this.show = false;
      }
    }
  }
}
