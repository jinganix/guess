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

import io.github.jinganix.guess.proto.internal.weapp.WeappCheckContentRequest;
import io.github.jinganix.guess.proto.internal.weapp.WeappCheckContentResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** WeappProvider. */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeappProvider {

  private static final int INVALID_ACCESS_TOKEN = 40001;

  private final AccessTokenCache accessTokenCache;

  private final WeappClient weappClient;

  public boolean checkTexts(List<String> texts) {
    String text = texts.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining("-"));
    if (StringUtils.isEmpty(text)) {
      return true;
    }
    return checkContent(text);
  }

  /**
   * Check content.
   *
   * @param content content
   * @return true if valid
   */
  public boolean checkContent(String content) {
    if (StringUtils.isEmpty(content)) {
      return true;
    }

    String accessToken = accessTokenCache.getAccessToken();
    WeappCheckContentRequest request = new WeappCheckContentRequest(accessToken, content);
    WeappCheckContentResponse response =
        weappClient.request(request, WeappCheckContentResponse.class);
    Integer errCode = response.getErrCode();
    if (errCode == null) {
      return true;
    }
    if (errCode == INVALID_ACCESS_TOKEN) {
      return true;
    }
    if (errCode == 43101) {
      log.info("Check message 43101: {}", response.getErrMsg());
    }
    if (errCode != 0) {
      log.warn("Check message error: {}", response.getErrMsg());
      return false;
    }
    return true;
  }
}
