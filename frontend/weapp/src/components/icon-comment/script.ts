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
import { cacheService, components } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { CommentEditorScript } from "@comps/comment-editor/script";
import { formatUrl, classId } from "@helpers/utils/utils";
import { Pages } from "@helpers/const";

const CONNECTOR = new Connector({ moment: DataPiker.spread<Moment>(["comment"]) });

interface Source extends SourceType<typeof CONNECTOR> {}

export class IconCommentScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  cacheKey = "";

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return IconCommentScript.CLASS_ID;
  }

  source(): Source {
    return {
      moment: (this.cacheKey && cacheService.getByKey(this.cacheKey)) || Moment.INSTANCE,
    };
  }

  tapIcon(): void {
    const moment = cacheService.getByKey<Moment>(this.cacheKey);
    const editor = components.findInPage(CommentEditorScript, this.pageId);
    if (editor) {
      editor.open(moment.id, "");
    } else {
      void wx.navigateTo({ url: formatUrl(Pages.MOMENT_DETAIL, { momentId: moment.id }) });
    }
  }
}
