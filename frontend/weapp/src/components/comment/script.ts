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

import { CommentEditorScript } from "@comps/comment-editor/script";
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { Comment } from "@modules/comment/comment.types";
import { cacheService, components, userStore } from "@modules/container";
import { User } from "@modules/user/user.types";
import { observable, observe } from "mobx";
import { PICKS } from "./pick";

export class CommentScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor cacheKey = "";
  @observable accessor comment: Comment | null = null;
  @observable accessor user: User | null = null;

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
    observe(this, "cacheKey", ({ newValue }) => {
      this.comment = cacheService.getByKey<Comment>(newValue);
    });
    observe(this, "comment", ({ newValue }) => {
      this.user = newValue ? cacheService.get<User>(User, newValue.toUserId!) : null;
    });
  }

  classId(): string {
    return CommentScript.CLASS_ID;
  }

  tapComment(): void {
    const comment = cacheService.getByKey<Comment>(this.cacheKey);
    const toCommentId = comment.userId === userStore.userId ? "" : comment.id;
    components.getInPage(CommentEditorScript, this.pageId).open(comment.momentId, toCommentId);
  }
}
