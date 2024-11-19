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

const ROOT_CLASS = "w-14 h-14 flex items-center justify-center border border-grey-3 rounded";
const ICON_CLASS = "w-[100%] h-[100%] rounded";

const CONNECTOR = new Connector({
  store: DataPiker.spread<AvatarScript>(["rootClass", "iconClass"]),
  user: DataPiker.spread<User>(["avatar"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class AvatarScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  userId = "";
  rootClass = "";
  iconClass = "";

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  onPropertyChanged<K extends keyof AvatarScript>(key: K, value: AvatarScript[K]): void {
    if (key === "rootClass") {
      this.rootClass = cn(ROOT_CLASS, value);
    } else if (key === "iconClass") {
      this.iconClass = cn(ICON_CLASS, value);
    } else {
      super.onPropertyChanged(key, value);
    }
  }

  classId(): string {
    return AvatarScript.CLASS_ID;
  }

  source(): Source {
    return {
      store: this,
      user: (this.userId && cacheService.get(User, this.userId)) || User.INSTANCE,
    };
  }

  tapAvatar(): void {
    this._comp.triggerEvent("tap", this.userId);
  }
}
