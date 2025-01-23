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
import { cacheService } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { observable, observe } from "mobx";
import { PICKS } from "./pick";

export class MomentFacadeScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor cacheKey = "";
  @observable accessor moment: Moment | null = null;

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
    observe(this, "cacheKey", ({ newValue }) => {
      this.moment = newValue ? cacheService.getByKey<Moment>(this.cacheKey) : null;
    });
  }

  classId(): string {
    return MomentFacadeScript.CLASS_ID;
  }
}
