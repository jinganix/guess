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

import { ErrorCode } from "@proto/AppErrorProto";
import { emitter, NotifierVariant } from "@helpers/event/emitter";
import { Dispose } from "@helpers/types/types";

export const errors: Record<string, string> = {
  [ErrorCode.OK]: "成功",
  [ErrorCode.ERROR]: "错误",
  [ErrorCode.BAD_REQUEST]: "错误的请求",
  [ErrorCode.PERMISSION_DENIED]: "没有权限",
  [ErrorCode.INVALID_OPERATION]: "无效的操作",
  [ErrorCode.DUPLICATED_OPERATION]: "重复的操作",
  [ErrorCode.BAD_CREDENTIAL]: "账号或密码错误",
  [ErrorCode.OPERATION_TOO_FREQUENT]: "操作太频繁，请稍后再试",
  [ErrorCode.USER_SUSPENDED]: "用户被冻结",
  [ErrorCode.BAD_REFRESH_TOKEN]: "无效的刷新令牌",
  [ErrorCode.WEAPP_APP_ID_NOT_FOUND]: "无效的小程序APP ID",
  [ErrorCode.WEAPP_LOGIN_FAILED]: "登录失败",
  [ErrorCode.WEPPP_INVALID_UER_DATA]: "无效的用户数据",
  [ErrorCode.WEAPP_CREATE_ORDER_FAILED]: "创建订单失败",
  [ErrorCode.WEAPP_REFUND_FAILED]: "退款失败",
  [ErrorCode.WEAPP_INVALID_CONTENT]: "含有违规内容",
  [ErrorCode.USER_NOT_FOUND]: "用户不存在",
  [ErrorCode.SUBSCRIBE_MESSAGE_SEND_FAILED]: "通知失败",
  [ErrorCode.REFUND_TIME_EXPIRED]: "超过最晚退款时间",
  [ErrorCode.TRY_ANSWER_EXCEED_LIMIT]: "超过次数限制",
  [ErrorCode.DUPLICATED_ANSWER]: "重复的答案",
  [ErrorCode.MOMENT_NOT_FOUND]: "动态不存在",
  [ErrorCode.COMMENT_NOT_FOUND]: "评论不存在",
  [ErrorCode.IMAGE_NOT_FOUND]: "图片不存在",
};

export function listenErrors(): Dispose {
  return emitter.on("notifier", (variant: NotifierVariant, content: string) => {
    if (content === String(ErrorCode.BAD_REFRESH_TOKEN)) {
      return;
    }
    content = errors[content] || content;
    const icon = {
      error: "error",
      info: "none",
      success: "success",
    }[variant] as "error" | "none" | "success";
    void wx.showToast({ icon, title: content });
  });
}
