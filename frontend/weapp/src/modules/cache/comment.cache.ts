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

import { Dispose } from "@helpers/types/types";
import { CacheHolder } from "@modules/cache/cache.types";
import { Comment } from "@modules/comment/comment.types";
import { find } from "lodash";
import { components } from "@modules/container";
import { CommentListScript } from "@comps/comment-list/script";

export class CommentCache implements CacheHolder<Comment> {
  constructor() {}

  dispose(): void {}

  fetchData(): Promise<void> {
    return Promise.resolve();
  }

  async initialize(): Promise<Dispose[]> {
    return Promise.resolve([() => this.dispose()]);
  }

  private findInStore(id: string): Comment | undefined {
    for (const script of components.getAll(CommentListScript)) {
      const item = find(script.comments, (x) => x.id === id);
      if (item) {
        return item;
      }
    }
    return undefined;
  }

  getOrCreate(id: string): Comment {
    const comment = this.findInStore(id);
    return comment ? comment : new Comment();
  }

  get(id: string): Comment {
    const comment = this.findInStore(id);
    if (!comment) {
      throw new Error(`Uncached comment id: ${id}`);
    }
    return comment;
  }

  set(id: string, item: Comment): void {
    const comment = this.findInStore(id);
    comment && Object.assign(comment, item);
  }

  remove(id: string): void {
    for (const script of components.getAll(CommentListScript)) {
      script.remove(id);
    }
  }
}
