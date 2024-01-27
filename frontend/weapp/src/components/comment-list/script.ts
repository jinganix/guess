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
import { Comment } from "@modules/comment/comment.types";
import { httpService } from "@helpers/service/http.service";
import { CommentListRequest, CommentListResponse, ICommentFacadePb } from "@proto/CommentProto";
import { CacheKey } from "@modules/cache/cache.service";
import { classId } from "@helpers/utils/utils";

const CONNECTOR = new Connector({
  store: DataPiker.spread<CommentListScript>(["cacheKeys", "loading", "more"]),
});

interface Source extends SourceType<typeof CONNECTOR> {}

export class CommentListScript extends ComponentScript<Source> {
  static readonly CLASS_ID = classId();
  cacheKey = "";
  loading = true;
  more = true;
  comments: Comment[] = [];
  cacheKeys: string[] = [];

  constructor(comp: ScriptedComponent) {
    super(comp, CONNECTOR);
    makePublicObservable(this);
  }

  classId(): string {
    return CommentListScript.CLASS_ID;
  }

  source(): Source {
    return { store: this };
  }

  didMount(): void {
    super.didMount();
    void this.fetchData(true);
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
