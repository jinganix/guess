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
import { ScriptedComponent } from "@helpers/wx/adapter";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";

const CONNECTOR = new Connector({
  store: DataPiker.spread<PopoverPreviewScript>(["show"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class PopoverPreviewScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  private _timer = 0;
  show = false;

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return PopoverPreviewScript.CLASS_ID;
  }

  source(): Source {
    return { store: this };
  }

  open(): void {
    this.show = true;
    this._timer = setTimeout(() => (this.show = false), 3000) as unknown as number;
  }

  tapClose(): void {
    this.show = false;
    if (this._timer) {
      clearTimeout(this._timer);
      this._timer = 0;
    }
  }
}
