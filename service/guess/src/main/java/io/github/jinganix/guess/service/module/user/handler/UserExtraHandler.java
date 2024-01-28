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

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.proto.service.user.UserExtraResponse;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.module.user.UserMapper;
import io.github.jinganix.guess.service.module.user.model.UserExtra;
import io.github.jinganix.guess.service.module.user.repository.UserExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserExtraHandler {

  private final UserMapper userMapper;

  private final UserExtraRepository userExtraRepository;

  public UserExtraResponse handle(Long userId) {
    UserExtra extra =
        userExtraRepository
            .findById(userId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    return new UserExtraResponse(userMapper.mapToPb(extra));
  }
}
