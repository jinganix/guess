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

import { AuthLoginRequest, AuthTokenRequest, AuthTokenResponse } from "@proto/AuthProto";
import { AuthToken, deleteAuthToken, readAuthToken, saveAuthToken } from "@helpers/token";
import { environment } from "@helpers/environment";
import { WebpbMessage } from "webpb";
import { request } from "@helpers/service/request";
import { Replay } from "@helpers/promise/replay";
import { awx } from "@helpers/wx/awx";
import { emitter } from "@helpers/event/emitter";

async function requestToken(message: WebpbMessage): Promise<AuthToken | null> {
  const res = await request({
    headers: {
      "content-type": "application/json;charset=utf-8",
    },
    host: environment.host,
    message,
    responseType: AuthTokenResponse,
  });
  if (!res) {
    return null;
  }
  const token = new AuthToken();
  token.accessToken = res.accessToken;
  token.refreshToken = res.refreshToken;
  token.expiresIn = res.expiresIn;
  token.groups = res.scope.split(",").filter((x) => !!x);
  return token;
}

export class AuthService {
  private replay = new Replay<AuthToken | null>(readAuthToken());

  constructor() {
    emitter.on("token", (token) => (this.replay = new Replay<AuthToken | null>(token)));
  }

  async ensureToken(): Promise<AuthToken | null> {
    const token = await this.getToken();
    return token ? token : this.auth();
  }

  async auth(): Promise<AuthToken | null> {
    const token = await this.replay.resolve(async () => {
      const { code } = await awx.login({ timeout: 5000 });
      return requestToken(AuthLoginRequest.create({ code }));
    });
    if (token) {
      await saveAuthToken(token);
    } else {
      this.replay = new Replay();
    }
    return token;
  }

  async getToken(): Promise<AuthToken | null> {
    const token = await this.replay.value();
    return token && token.isExpired() ? this.refresh(token.refreshToken) : token;
  }

  async refresh(refreshToken: string): Promise<AuthToken | null> {
    const token = await this.replay.resolve(
      () => requestToken(AuthTokenRequest.create({ refreshToken })),
      "refresh",
    );
    if (token) {
      await saveAuthToken(token);
      return token;
    }
    this.replay = new Replay();
    await deleteAuthToken();
    return null;
  }
}
