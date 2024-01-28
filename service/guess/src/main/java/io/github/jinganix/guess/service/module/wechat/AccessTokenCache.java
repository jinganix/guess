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

package io.github.jinganix.guess.service.module.wechat;

import io.github.jinganix.guess.proto.internal.weapp.WeappAccessTokenRequest;
import io.github.jinganix.guess.proto.internal.weapp.WeappAccessTokenResponse;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** WechatCache. */
@Component
@RequiredArgsConstructor
public class AccessTokenCache {

  @Value("${core.weapp.app-id}")
  private String appId;

  @Value("${core.weapp.app-secret}")
  private String appSecret;

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final UtilsService utilsService;

  private final WeappClient weappClient;

  private String accessToken;

  private long createdAt;

  /**
   * Get access token.
   *
   * @return access token.
   */
  public String getAccessToken() {
    long millis = utilsService.currentTimeMillis();
    if (accessToken != null && createdAt + TimeUnit.MINUTES.toMillis(30) > millis) {
      return this.accessToken;
    }
    return orderedTraceExecutor.supply(
        "ACCESS_TOKEN",
        () -> {
          WeappAccessTokenRequest request = new WeappAccessTokenRequest(appId, appSecret);
          WeappAccessTokenResponse response =
              weappClient.request(request, WeappAccessTokenResponse.class);
          if (response.getErrCode() != null && response.getErrCode() != 0) {
            throw ApiException.of(ErrorCode.ERROR);
          }
          this.accessToken = response.getAccessToken();
          this.createdAt = millis;
          return this.accessToken;
        });
  }
}
