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

import { CommentListScript } from "@comps/comment-list/script";
import { emitter } from "@helpers/event/emitter";
import { httpService } from "@helpers/service/http.service";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { Input } from "@helpers/wx/wx.types";
import { cacheService, components } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import { CommentCreateRequest, CommentCreateResponse } from "@proto/CommentProto";
import { observable } from "mobx";
import { PICKS } from "./pick";

export class CommentEditorScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor commentId = "";
  @observable accessor content = "";
  @observable accessor loading = false;
  @observable accessor momentId = "";
  @observable accessor show = false;

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
  }

  classId(): string {
    return CommentEditorScript.CLASS_ID;
  }

  open(momentId: string, commentId: string): void {
    this.show = true;
    this.loading = false;
    this.momentId = momentId;
    this.commentId = commentId;
    this.content = "";
  }

  async tapConfirm(): Promise<void> {
    this.loading = true;
    if (this.content.length < 3) {
      void emitter.emit("notifier", "info", "评论至少需要3个字符");
      return;
    }
    const res = await httpService.request(
      CommentCreateRequest.create({
        commentId: this.commentId,
        content: this.content,
        momentId: this.momentId,
      }),
      CommentCreateResponse,
    );
    if (res) {
      components.getAll(CommentListScript).forEach((x) => x.prepend(res.comment));
      const moment = cacheService.get(Moment, res.comment.comment.momentId);
      moment.updateComment(moment.comment + 1);
      this.show = false;
    }
    this.loading = false;
  }

  onClose(): void {
    this.show = false;
  }

  onInput(ev: Input): void {
    this.content = ev.detail.value;
  }
}
