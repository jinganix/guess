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

import { PopoverShareScript } from "@comps/popup-share/script";
import { UserEditScript } from "@comps/user-edit/script";
import { Pages } from "@helpers/const";
import { classId, formatUrl } from "@helpers/utils/utils";
import { ScriptedPage } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { appService, components, configStore, userExtraStore, userStore } from "@modules/container";
import { MomentCategory } from "@proto/MomentProto";
import { observable } from "mobx";
import { PICKS } from "./pick";

export class UserDetailScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor configStore = configStore;
  @observable accessor userExtraStore = userExtraStore;
  @observable accessor userStore = userStore;

  constructor(comp: ScriptedPage) {
    super(comp, PICKS);
  }

  classId(): string {
    return UserDetailScript.CLASS_ID;
  }

  didMount(): void {
    super.didMount();
    void userExtraStore.fetchData();
  }

  tapAvatar(): void {
    components.getInPage(UserEditScript, this.pageId).checkOpen();
  }

  tapRepo(): void {
    void appService.copyText("https://github.com/jinganix/guess");
  }

  tapEdit(): void {
    components.getInPage(UserEditScript, this.pageId).open();
  }

  tapInvite(): void {
    components.getInPage(PopoverShareScript, this.pageId).open();
  }

  tapFollowedPage(): void {
    void wx.navigateTo({
      url: formatUrl(Pages.USER_MOMENTS, { category: MomentCategory.FOLLOWED }),
    });
  }

  tapMomentPage(): void {
    void wx.navigateTo({
      url: formatUrl(Pages.USER_MOMENTS, { category: MomentCategory.MINE }),
    });
  }
}
