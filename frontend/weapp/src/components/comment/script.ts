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
import { cacheService, components, userStore } from "@modules/container";
import { Comment } from "@modules/comment/comment.types";
import { classId } from "@helpers/utils/utils";
import { CommentEditorScript } from "@comps/comment-editor/script";
import { User } from "@modules/user/user.types";

const CONNECTOR = new Connector({
  comment: DataPiker.spread<Comment>(["content", "created", "userId"]),
  user: DataPiker.spread<User>(["nickname"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class CommentScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  cacheKey = "";

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return CommentScript.CLASS_ID;
  }

  source(): Source {
    if (!this.cacheKey) {
      return { comment: Comment.INSTANCE, user: User.INSTANCE };
    }
    const comment = cacheService.getByKey<Comment>(this.cacheKey);
    if (!comment.toUserId || comment.toUserId === comment.userId) {
      return { comment, user: User.INSTANCE };
    }
    return { comment, user: cacheService.get<User>(User, comment.toUserId) };
  }

  tapComment(): void {
    const comment = cacheService.getByKey<Comment>(this.cacheKey);
    const toCommentId = comment.userId === userStore.userId ? "" : comment.id;
    components.getInPage(CommentEditorScript, this.pageId).open(comment.momentId, toCommentId);
  }
}
