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

import { environment } from "@helpers/environment";
import { AuthService } from "@helpers/service/auth.service";
import { request as httpRequest } from "@helpers/service/request";
import { WebpbMessage } from "webpb";

class HttpService {
  url: string = "";

  private authService: AuthService = new AuthService();

  async request<R extends WebpbMessage>(
    message: WebpbMessage,
    responseType?: { prototype: R },
  ): Promise<R | null> {
    const token = await this.authService.ensureToken();
    if (!token) {
      return new Promise(() => null);
    }
    return await httpRequest({
      headers: {
        authorization: `Bearer ${token.accessToken}`,
        "content-type": "application/json;charset=utf-8",
      },
      host: environment.host,
      message,
      responseType,
    });
  }
}

export const httpService = new HttpService();
