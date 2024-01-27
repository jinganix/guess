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

const TOKEN_KEY = "AUTH_TOKEN";

export class AuthToken {
  createdAt = 0;
  accessToken = "";
  expiresIn = 0;
  refreshToken = "";
  groups: string[] = [];

  constructor(createdAt = Date.now()) {
    this.createdAt = createdAt;
  }

  isExpired(): boolean {
    return this.createdAt + 1000 * this.expiresIn - 30000 < Date.now();
  }
}

export function readAuthToken(): AuthToken | null {
  const data = wx.getStorageSync<string>(TOKEN_KEY);
  try {
    return data ? (Object.assign(new AuthToken(), JSON.parse(data)) as AuthToken) : null;
  } catch (e) {
    return null;
  }
}

export async function saveAuthToken(token: AuthToken): Promise<void> {
  wx.setStorageSync(TOKEN_KEY, JSON.stringify(token));
  await emitter.emit("token", token);
}

export async function deleteAuthToken(): Promise<void> {
  wx.removeStorageSync(TOKEN_KEY);
  await emitter.emit("token", null);
}
