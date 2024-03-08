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

import { WebpbMessage, WebpbMeta } from "webpb";
import { environment } from "@helpers/environment";
import { awx } from "@helpers/wx/awx";
import { emitter } from "@helpers/event/emitter";
import { RequestOption } from "@helpers/wx/wx.types";
import { urlJoin } from "@helpers/utils/url.join";
import { ErrorCode } from "@proto/AppErrorProto";

export interface RequestConfig<R extends WebpbMessage> {
  message: WebpbMessage;
  responseType?: { prototype: R };
  host: string;
  headers: Record<string, string>;
}

function formatUrl(meta: WebpbMeta, url?: string): string {
  const baseUrl = (url ? url : environment.host) || "";
  const context = meta.context || "";
  const path = meta.path || "";
  return urlJoin(baseUrl, context, path);
}

async function checkError(status: number, data: Record<string, unknown>): Promise<boolean> {
  if (status >= 200 && status < 300) {
    return false;
  }

  if (status == 401) {
    throw ErrorCode.FORCE_LOGOUT;
  }
  if (status == 403) {
    await emitter.emit("notifier", "error", "没有权限");
  } else if (status == 404) {
    await emitter.emit("notifier", "error", "无效的接口");
  } else if (data?.code) {
    await emitter.emit("notifier", "info", String(data.code));
  } else {
    await emitter.emit("notifier", "info", "请求失败，请重试");
  }
  return true;
}

export async function request<R extends WebpbMessage>(config: RequestConfig<R>): Promise<R | null> {
  console.log(config.message);
  const meta = config.message.webpbMeta();
  const url = formatUrl(meta, config.host);
  let res;
  try {
    res = await awx.request({
      data: JSON.stringify(config.message.toWebpbAlias()),
      header: config.headers,
      method: meta.method as RequestOption["method"],
      timeout: 5000,
      url,
    });
  } catch (err) {
    await emitter.emit("notifier", "info", "请求失败，请稍后重试");
    throw err;
  }
  const data = res.data as Record<string, unknown>;
  if (await checkError(res.statusCode, data)) {
    return null;
  }
  const responseType = config.responseType as unknown as {
    fromAlias: (data: unknown) => R;
  };
  const { fromAlias } = responseType || {};
  const response = fromAlias ? fromAlias(data) : (data as R);
  console.log(response);
  return response;
}
