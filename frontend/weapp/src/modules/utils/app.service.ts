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

import { ICustomShareContent, ICustomTimelineContent } from "@helpers/wx/wx.types";
import { relativePrefix } from "@helpers/utils/utils";
import { Pages } from "@helpers/const";

export class AppService {
  async copyText(text: string): Promise<void> {
    try {
      await wx.setClipboardData({ data: text });
      void wx.showToast({ icon: "success", title: "复制成功" });
    } catch (err) {
      void wx.showToast({ icon: "error", title: "复制失败" });
    }
  }

  share(route: string): ICustomShareContent | void {
    console.log({
      imageUrl: `${relativePrefix(route)}assets/images/puzzle.png`,
      path: `${Pages.PUZZLE_DETAIL}`,
      title: "看图猜成语",
    });
    return {
      imageUrl: `${relativePrefix(route)}assets/images/puzzle.png`,
      path: `${Pages.PUZZLE_DETAIL}`,
      title: "看图猜成语",
    };
  }

  shareTimeline(route: string): ICustomTimelineContent | void {
    return {
      imageUrl: `${relativePrefix(route)}assets/images/puzzle.png`,
      title: "看图猜成语",
    };
  }
}
