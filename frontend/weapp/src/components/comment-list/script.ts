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
import { classId } from "@helpers/utils/utils";
import { ScriptedComponent } from "@helpers/wx/adapter.types";
import { ComponentScript } from "@helpers/wx/component.script";
import { CacheKey } from "@modules/cache/cache.service";
import { Comment } from "@modules/comment/comment.types";
import { CommentListRequest, CommentListResponse, ICommentFacadePb } from "@proto/CommentProto";
import { observable, observe } from "mobx";
import { PICKS } from "./pick";

export class CommentListScript extends ComponentScript {
  static readonly CLASS_ID = classId();
  @observable accessor cacheKey = "";
  @observable accessor loading = true;
  @observable accessor more = true;
  @observable accessor comments: Comment[] = [];
  @observable accessor cacheKeys: string[] = [];

  constructor(comp: ScriptedComponent) {
    super(comp, PICKS);
    observe(this, "cacheKey", () => void this.fetchData(true));
  }

  classId(): string {
    return CommentListScript.CLASS_ID;
  }

  async fetchData(reset: boolean): Promise<void> {
    if ((!this.more || !this.cacheKey) && !reset) {
      return;
    }
    this.loading = true;
    const comments = reset ? [] : this.comments;
    const comment = comments[this.comments.length - 1];
    const res = await httpService.request(
      CommentListRequest.create({
        createdAt: comment?.createdAt ?? 0,
        id: comment?.id ?? "",
        momentId: CacheKey.fromKey(this.cacheKey).id,
      }),
      CommentListResponse,
    );
    if (res) {
      this.more = res.more;
      this.append(comments, res.comments);
    }
    this.loading = false;
  }

  private setComments(comments: Comment[]): void {
    this.comments = comments;
    this.cacheKeys = this.comments.map((x) => x.cacheKey);
  }

  append(items: Comment[], pbs: ICommentFacadePb[]): void {
    this.setComments([...items, ...pbs.map((x) => Comment.fromPb(x))]);
  }

  prepend(pb: ICommentFacadePb): void {
    this.setComments([Comment.fromPb(pb), ...this.comments]);
  }

  remove(commentId: string): void {
    this.setComments(this.comments.filter((e) => e.id !== commentId));
  }
}
