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

import { cn } from "@helpers/utils/cn";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { cacheService } from "@modules/container";
import { User } from "@modules/user/user.types";
import { intercept, observable, observe } from "mobx";
import { PICKS } from "./pick";

export class AvatarScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor userId = "";
  @observable accessor rootClass = "";
  @observable accessor iconClass = "";
  @observable accessor user: User | null = null;

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
    intercept(this, "rootClass", (change) => {
      change.newValue = cn(
        "w-14 h-14 flex items-center justify-center border border-grey-3 rounded",
        change.newValue,
      );
      return change;
    });
    intercept(this, "iconClass", (change) => {
      change.newValue = cn("w-[100%] h-[100%] rounded", change.newValue);
      return change;
    });
    observe(this, "userId", ({ newValue }) => {
      this.user = newValue ? cacheService.get(User, newValue) : null;
    });
  }

  classId(): string {
    return AvatarScript.CLASS_ID;
  }

  tapAvatar(): void {
    this._comp.triggerEvent("tap", this.userId);
  }
}
