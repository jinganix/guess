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

import { emitter } from "@helpers/event/emitter";
import { httpService } from "@helpers/service/http.service";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { CustomEvent, Input, TappedEvent } from "@helpers/wx/wx.types";
import { cacheService, userStore } from "@modules/container";
import { User } from "@modules/user/user.types";
import { UserUpdateRequest, UserUpdateResponse } from "@proto/UserProto";
import { observable } from "mobx";
import { PICKS } from "./pick";

export type Gender = 0 | 1 | 2;

export class UserEditScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor loading = false;
  @observable accessor avatar = "";
  @observable accessor nickname = "";
  @observable accessor gender: Gender = 0;
  @observable accessor show = false;

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
  }

  classId(): string {
    return UserEditScript.CLASS_ID;
  }

  willUnmount(): void {
    super.willUnmount();
    this.avatar = "";
    this.nickname = "";
    this.gender = 0;
    this.show = false;
  }

  open(): void {
    this.show = true;
    const user = cacheService.get(User, userStore.userId);
    this.avatar = user.avatar;
    this.nickname = user.nickname;
    this.gender = user.gender as Gender;
  }

  checkOpen(): void {
    const user = cacheService.get(User, userStore.userId);
    user.avatar || this.open();
  }

  changeNickname(ev: Input): void {
    this.nickname = ev.detail.value;
  }

  chooseAvatar(ev: CustomEvent<{ avatarUrl: string }>): void {
    this.avatar = `data:image/jpeg;base64,${String(
      wx.getFileSystemManager().readFileSync(ev.detail.avatarUrl, "base64"),
    )}`;
  }

  tapGender(ev: TappedEvent<{ gender: string }>): void {
    this.gender = Number(ev.currentTarget.dataset.gender) as Gender;
  }

  async tapConfirm(): Promise<void> {
    this.loading = true;
    const res = await httpService.request(
      UserUpdateRequest.create({
        avatar: this.avatar,
        gender: this.gender,
        nickName: this.nickname,
      }),
      UserUpdateResponse,
    );
    if (res) {
      userStore.update(res.user);
      void emitter.emit("notifier", "info", "保存成功");
      this.show = false;
    }
    this.loading = false;
  }

  tapClose(): void {
    this.show = false;
  }
}
