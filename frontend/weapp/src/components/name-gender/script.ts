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

export class NameGenderScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor userId = "";
  @observable accessor user: User | null = null;
  @observable accessor nameClass = "";

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
    intercept(this, "nameClass", (change) => {
      change.newValue = cn("text-base font-bold text-grey-0", change.newValue);
      return change;
    });
    observe(this, "userId", ({ newValue }) => {
      this.user = newValue ? cacheService.get(User, newValue) : null;
    });
  }

  classId(): string {
    return NameGenderScript.CLASS_ID;
  }
}
