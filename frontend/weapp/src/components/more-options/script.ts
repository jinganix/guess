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

import { PopupOptionsScript } from "@comps/popup-options/script";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { components } from "@modules/container";

export class MoreOptionsScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  cacheKey = "";

  constructor(comp: ScriptedComponent) {
    super(comp);
    makePublicObservable(this);
  }

  classId(): string {
    return MoreOptionsScript.CLASS_ID;
  }

  source(): object {
    return {};
  }

  tapDots(): void {
    components.getInPage(PopupOptionsScript, this.pageId).open(this.cacheKey);
  }
}
