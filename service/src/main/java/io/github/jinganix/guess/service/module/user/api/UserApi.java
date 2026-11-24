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

package io.github.jinganix.guess.service.module.user.api;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.user.model.User;
import io.github.jinganix.guess.service.module.user.model.UserStatus;
import io.github.jinganix.guess.service.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Cross-module boundary of the user domain (see {@code user/api} named interface): other modules
 * must not reach into {@code user.model} / {@code user.repository} directly.
 */
@Service
@RequiredArgsConstructor
public class UserApi {

  private final UserRepository userRepository;

  private final UtilsService utilsService;

  /** Creates (or overwrites) the user row for a login; callers already hold the user id. */
  public void createUser(long userId) {
    long millis = utilsService.currentTimeMillis();
    User user =
        (User)
            new User()
                .setId(userId)
                .setStatus(UserStatus.ACTIVE)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    userRepository.save(user);
  }

  /** Fails with {@code USER_NOT_FOUND} when the user row is absent. */
  public void assertUserExists(long userId) {
    userRepository.findById(userId).orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
  }
}
