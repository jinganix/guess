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
import { ScriptedComponent } from "@helpers/wx/adapter";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { cacheService } from "@modules/container";
import { User } from "@modules/user/user.types";

const NAME_CLASS = "text-base font-bold text-grey-0";

const CONNECTOR = new Connector({
  store: DataPiker.spread<NameGenderScript>(["nameClass"]),
  user: DataPiker.spread<User>(["nickname", "gender"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class NameGenderScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  userId = "";
  nameClass = "";

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return NameGenderScript.CLASS_ID;
  }

  source(): Source {
    return {
      store: this,
      user: (this.userId && cacheService.get(User, this.userId)) || User.INSTANCE,
    };
  }

  onPropertyChanged<K extends keyof NameGenderScript>(key: K, value: NameGenderScript[K]): void {
    if (key === "nameClass") {
      this.nameClass = cn(NAME_CLASS, value);
    } else {
      super.onPropertyChanged(key, value);
    }
  }
}
