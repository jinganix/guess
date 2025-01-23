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

import { PopoverPreviewScript } from "@comps/popover-preview/script";
import { BehaviorOption, MethodOption, PageDataOption, PropertyOption } from "@helpers/wx/wx.types";
import { components } from "@modules/container";

Component<PageDataOption, PropertyOption, MethodOption, BehaviorOption>({
  data: {
    options: ["天外有天", "指鹿为马", "鸡飞狗跳", "火上浇油"],
  },

  methods: {
    ready(): void {
      this.showPopover();
    },

    showPopover(): void {
      components.getInPage(PopoverPreviewScript, this.getPageId()).open();
    },
  },

  options: {
    styleIsolation: "apply-shared",
  },
});
