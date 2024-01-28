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

import {
  IMomentFacadePb,
  MomentAnswerRequest,
  MomentAnswerResponse,
  MomentDeleteRequest,
  MomentDeleteResponse,
  MomentFollowRequest,
  MomentFollowResponse,
  MomentLikeRequest,
  MomentLikeResponse,
  MomentReportRequest,
  MomentReportResponse,
  MomentStatus,
} from "@proto/MomentProto";
import { makeAutoObservable } from "mobx";
import { formatReadable, classId } from "@helpers/utils/utils";
import { httpService } from "@helpers/service/http.service";
import { emitter } from "@helpers/event/emitter";
import { CacheItem } from "@modules/cache/cache.types";
import { CacheKey } from "@modules/cache/cache.service";
import { cacheService, userExtraStore, userStore } from "@modules/container";

export class Moment implements CacheItem {
  static readonly CLASS_ID = classId();
  static readonly INSTANCE = new Moment();
  cacheKey = "";
  id = "";
  userId = "";
  content = "";
  options: string[] = [];
  answer = 0;
  correct = false;
  status = MomentStatus.ACTIVE;
  comment = 0;
  follow = 0;
  like = 0;
  followed = false;
  liked = false;
  reported = false;
  created = "";
  createdAt = 0;

  constructor() {
    makeAutoObservable(this);
  }

  toKey(): CacheKey {
    return CacheKey.fromType(Moment, this.id);
  }

  static fromPb(pb: IMomentFacadePb): Moment {
    return cacheService.getOrCreate(Moment, pb.moment.id).update(pb);
  }

  copy(moment: Moment): void {
    Object.assign(this, moment);
  }

  update({ moment, action, answer }: IMomentFacadePb): Moment {
    this.id = moment.id;
    this.cacheKey = this.toKey().toString();
    this.userId = moment.userId;
    this.content = moment.content;
    this.options = [moment.option1, moment.option2, moment.option3, moment.option4].filter(Boolean);
    this.status = moment.status;
    this.comment = Math.max(0, moment.comment);
    this.answer = answer;
    this.follow = Math.max(0, moment.follow);
    this.like = Math.max(0, moment.like);
    this.created = formatReadable(moment.createdAt);
    this.createdAt = moment.createdAt;
    this.correct = action?.correct ?? false;
    this.followed = action?.followed ?? 0;
    this.liked = action?.liked ?? 0;
    this.reported = action?.reported ?? 0;
    return this;
  }

  updateCorrect(correct: boolean): void {
    this.correct = correct;
  }

  updateComment(comment: number): void {
    this.comment = Math.max(0, comment);
  }

  async handleAnswer(answer: number): Promise<void> {
    const res = await httpService.request(
      MomentAnswerRequest.create({ answer, id: this.id }),
      MomentAnswerResponse,
    );
    if (res) {
      this.updateCorrect(res.correct);
      if (res.correct) {
        void emitter.emit("notifier", "success", "回答正确");
      } else {
        void emitter.emit("notifier", "error", "回答错误");
      }
    }
  }

  async delete(): Promise<boolean> {
    const res = await httpService.request(
      MomentDeleteRequest.create({ id: this.id }),
      MomentDeleteResponse,
    );
    if (res) {
      cacheService.remove(Moment, this.id);
      userExtraStore.updateMoment(userExtraStore.moment - 1);
      void emitter.emit("notifier", "info", "删除成功");
    }
    return !!res;
  }

  async toggleLike(): Promise<boolean> {
    const res = await httpService.request(
      MomentLikeRequest.create({ id: this.id }),
      MomentLikeResponse,
    );
    if (res) {
      res.liked ? this.like++ : this.like--;
      this.liked = res.liked;
    }
    return !!res;
  }

  async toggleReport(): Promise<boolean> {
    const res = await httpService.request(
      MomentReportRequest.create({ id: this.id }),
      MomentReportResponse,
    );
    if (res) {
      this.reported = res.reported;
      if (res.deleted) {
        cacheService.remove(Moment, this.id);
        this.userId === userStore.userId && userExtraStore.updateMoment(userExtraStore.moment - 1);
      }
    }
    return !!res;
  }

  async toggleFollow(): Promise<boolean> {
    if (this.status !== MomentStatus.ACTIVE && !this.followed) {
      return false;
    }
    const res = await httpService.request(
      MomentFollowRequest.create({ id: this.id }),
      MomentFollowResponse,
    );
    if (res) {
      res.followed ? this.follow++ : this.follow--;
      this.followed = res.followed;
      userExtraStore.updateFollow(userExtraStore.follow + (res.followed ? 1 : -1));
    }
    return !!res;
  }
}
