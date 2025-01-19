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

import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { CacheKey } from "@modules/cache/cache.service";
import { Comment } from "@modules/comment/comment.types";
import { cacheService, userStore } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { observable, observe } from "mobx";
import { PICKS } from "./pick";

export class PopupOptionsScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor cacheKey = "";
  @observable accessor show = false;
  @observable accessor moment: Moment | null = null;
  @observable accessor userStore = userStore;

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
    observe(this, "cacheKey", ({ newValue }) => {
      this.moment = newValue ? cacheService.getByKey<Moment>(newValue) : null;
    });
  }

  classId(): string {
    return PopupOptionsScript.CLASS_ID;
  }

  willUnmount(): void {
    super.willUnmount();
    this.cacheKey = "";
    this.show = false;
  }

  open(cacheKey: string): void {
    this.cacheKey = cacheKey;
    this.show = true;
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
