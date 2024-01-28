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
import io.github.jinganix.guess.proto.service.user.UserUpdateRequest;
import io.github.jinganix.guess.proto.service.user.UserUpdateResponse;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.user.UserMapper;
import io.github.jinganix.guess.service.module.user.model.User;
import io.github.jinganix.guess.service.module.user.model.UserStatus;
import io.github.jinganix.guess.service.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateHandler {

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  private final UtilsService utilsService;

  public UserUpdateResponse handle(Long userId, UserUpdateRequest request) {
    long millis = utilsService.currentTimeMillis();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    user.setGender(request.getGender())
        .setAvatar(request.getAvatar())
        .setNickname(request.getNickName())
        .setStatus(UserStatus.ACTIVE)
        .setUpdatedAt(millis);
    userRepository.save(user);
    return new UserUpdateResponse(userMapper.mapToPb(user));
  }
}
