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

import { Components } from "@helpers/wx/components";
import { CacheService } from "@modules/cache/cache.service";
import { CommentCache } from "@modules/cache/comment.cache";
import { MomentCache } from "@modules/cache/moment.cache";
import { UserCache } from "@modules/cache/user.cache";
import { Comment } from "@modules/comment/comment.types";
import { ConfigStore } from "@modules/config/config.store";
import { Moment } from "@modules/moment/moment.types";
import { UserExtraStore } from "@modules/user/user.extra.store";
import { UserStore } from "@modules/user/user.store";
import { User } from "@modules/user/user.types";
import { AppService } from "@modules/utils/app.service";

export const appService = new AppService();
export const cacheService = new CacheService();
export const components = new Components();
export const configStore = new ConfigStore();
export const userExtraStore = new UserExtraStore();
export const userStore = new UserStore();

cacheService.holders = {
  [User.CLASS_ID]: new UserCache(),
  [Moment.CLASS_ID]: new MomentCache(),
  [Comment.CLASS_ID]: new CommentCache(),
};
