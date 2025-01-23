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
import { formatReadable, classId } from "@helpers/utils/utils";
import { CacheKey } from "@modules/cache/cache.service";
import { CacheItem } from "@modules/cache/cache.types";
import { cacheService } from "@modules/container";
import { Moment } from "@modules/moment/moment.types";
import {
  CommentDeleteRequest,
  CommentDeleteResponse,
  CommentLikeRequest,
  CommentLikeResponse,
  CommentReportRequest,
  CommentReportResponse,
  ICommentFacadePb,
} from "@proto/CommentProto";
import { makeAutoObservable } from "mobx";

export class Comment implements CacheItem {
  static readonly CLASS_ID = classId();
  cacheKey = "";
  id = "";
  momentId = "";
  content = "";
  userId = "";
  toUserId? = "";
  like = 0;
  liked = false;
  reported = false;
  created = "";
  createdAt = 0;

  constructor() {
    makeAutoObservable(this);
  }

  toKey(): CacheKey {
    return CacheKey.fromType(Comment, this.id);
  }

  static fromPb(pb: ICommentFacadePb): Comment {
    return cacheService.getOrCreate(Comment, pb.comment.id).update(pb);
  }

  copy(comment: Comment): void {
    Object.assign(this, comment);
  }

  update({ comment, action }: ICommentFacadePb): Comment {
    this.id = comment.id;
    this.momentId = comment.momentId;
    this.cacheKey = this.toKey().toString();
    this.userId = comment.userId;
    this.toUserId = comment.toUserId;
    this.content = comment.content;
    this.like = Math.max(0, comment.like);
    this.created = formatReadable(comment.createdAt);
    this.createdAt = comment.createdAt;
    this.liked = action?.liked ?? 0;
    this.reported = action?.reported ?? 0;
    return this;
  }

  async delete(): Promise<boolean> {
    const res = await httpService.request(
      CommentDeleteRequest.create({ id: this.id }),
      CommentDeleteResponse,
    );
    if (res) {
      cacheService.remove(Comment, this.id);
      const moment = cacheService.get(Moment, this.momentId);
      moment && moment.updateComment(moment.comment - 1);
      void emitter.emit("notifier", "info", "删除成功");
    }
    return !!res;
  }

  async toggleLike(): Promise<boolean> {
    const res = await httpService.request(
      CommentLikeRequest.create({ id: this.id }),
      CommentLikeResponse,
    );
    if (res) {
      res.liked ? this.like++ : this.like--;
      this.liked = res.liked;
    }
    return !!res;
  }

  async toggleReport(): Promise<boolean> {
    const res = await httpService.request(
      CommentReportRequest.create({ id: this.id }),
      CommentReportResponse,
    );
    res && (this.reported = res.reported);
    return !!res;
  }
}
