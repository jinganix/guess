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

import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { ScriptedComponent } from "@helpers/wx/adapter";
import { cn } from "@helpers/utils/cn";
import { classId } from "@helpers/utils/utils";

const ICON_CLASS = "text-2xl text-grey-0";

const CONNECTOR = new Connector({
  store: DataPiker.spread<MovableButtonScript>(["iconClass"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class MovableButtonScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  iconClass = "";

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return MovableButtonScript.CLASS_ID;
  }

  source(): Source {
    return { store: this };
  }

  onPropertyChanged<K extends keyof MovableButtonScript>(
    key: K,
    value: MovableButtonScript[K],
  ): void {
    if (key === "iconClass") {
      this.iconClass = cn(ICON_CLASS, value);
    } else {
      super.onPropertyChanged(key, value);
    }
  }

  tapButton(): void {
    this._comp.triggerEvent("click");
  }
}
