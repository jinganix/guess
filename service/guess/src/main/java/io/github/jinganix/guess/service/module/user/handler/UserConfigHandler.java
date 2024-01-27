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

package io.github.jinganix.guess.service.module.user.handler;

import io.github.jinganix.guess.proto.service.user.ConfigPb;
import io.github.jinganix.guess.proto.service.user.UserConfigResponse;
import io.github.jinganix.guess.service.helper.utils.ConfigProperties;
import io.github.jinganix.guess.service.module.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConfigHandler {

  private final ConfigProperties configProperties;

  private final UserMapper userMapper;

  public UserConfigResponse handle() {
    ConfigPb configPb = userMapper.mapToPb(configProperties);
    return new UserConfigResponse(configPb);
  }
}
