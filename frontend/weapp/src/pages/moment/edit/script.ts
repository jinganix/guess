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

import { httpService } from "@helpers/service/http.service";
import { emitter } from "@helpers/event/emitter";
import {
  IMomentCreateRequest,
  MomentCreateRequest,
  MomentCreateResponse,
} from "@proto/MomentProto";
import { every, some } from "lodash";
import { appService, components, userExtraStore } from "@modules/container";
import { ComponentScript, makePublicObservable } from "@helpers/wx/component.script";
import { Connector, DataPiker, SourceType } from "@helpers/wx/connect";
import { ScriptedPage } from "@helpers/wx/adapter";
import {
  CustomEvent,
  ICustomShareContent,
  ICustomTimelineContent,
  Input,
} from "@helpers/wx/wx.types";
import { classId, trimContent } from "@helpers/utils/utils";
import { MomentListScript } from "../list/script";

export class EditOptions {
  option1 = "";
  option2 = "";
  option3 = "";
  option4 = "";
}

const CONNECTOR = new Connector({
  store: DataPiker.align<MomentEditScript>(DataPiker.ALL),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class MomentEditScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  loading = false;
  activeNames: string[] = [];
  answer = 1;
  answers: { text: string; value: number }[] = [
    { text: "选项1", value: 1 },
    { text: "选项2", value: 2 },
    { text: "选项3", value: 3 },
    { text: "选项4", value: 4 },
  ];
  content = "";
  optionKeys = [1, 2, 3, 4].map((e) => `option${e}`);
  optionLen = 12;
  options = new EditOptions();

  constructor(comp: ScriptedPage) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return MomentEditScript.CLASS_ID;
  }

  source(): Source {
    return { store: this };
  }

  willUnmount(): void {
    super.willUnmount();
    this.loading = false;
    this.activeNames = [];
    this.answer = 1;
    this.answers = [
      { text: "选项1", value: 1 },
      { text: "选项2", value: 2 },
      { text: "选项3", value: 3 },
      { text: "选项4", value: 4 },
    ];
    this.content = "";
    this.optionKeys = [1, 2, 3, 4].map((e) => `option${e}`);
    this.optionLen = 12;
    this.options = new EditOptions();
  }

  onShareAppMessage(): ICustomShareContent | void {
    return appService.share((this._comp as ScriptedPage).route);
  }

  onShareTimeline(): ICustomTimelineContent | void {
    return appService.shareTimeline((this._comp as ScriptedPage).route);
  }

  onCollapse(): void {
    this.activeNames = this.activeNames.length ? [] : ["1"];
  }

  onDropdownChange(ev: CustomEvent): void {
    this.answer = ev.detail as unknown as number;
  }

  onInput(ev: Input): void {
    this.content = ev.detail.value;
  }

  onInputOption(ev: Input): void {
    const { value } = ev.detail;
    const { key, len } = ev.target.dataset as { key: string; len: number };
    this.options = {
      ...this.options,
      [key]: trimContent(value || "").substring(0, len),
    };
  }

  async tapSubmit(): Promise<void> {
    if (await this.create()) {
      await wx.navigateBack();
    }
  }

  async create(): Promise<boolean> {
    this.loading = true;
    if (this.content.length < 3) {
      void emitter.emit("notifier", "info", "主题至少需要3个字符");
      return false;
    }
    const values = Object.values(this.options);
    if (some(values, (e) => !!e) && some(values, (e) => !e)) {
      void emitter.emit("notifier", "info", "请输入所有选项");
      return false;
    }
    const answer = every(values, (e) => !e) ? 0 : this.answer;
    if (answer < 0 || answer > 4) {
      void emitter.emit("notifier", "info", "答案为1-4");
      return false;
    }
    const res = await httpService.request(
      MomentCreateRequest.create(
        Object.assign(
          {
            answer: this.answer,
            content: this.content,
          } as IMomentCreateRequest,
          this.options,
        ),
      ),
      MomentCreateResponse,
    );
    if (res) {
      components.getAll(MomentListScript).forEach((x) => x.prepend(res.moment));
      userExtraStore.updateMoment(userExtraStore.moment + 1);
      await emitter.emit("notifier", "success", "发布成功");
    }
    this.loading = false;
    return !!res;
  }
}
