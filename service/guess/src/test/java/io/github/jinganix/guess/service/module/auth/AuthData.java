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

package io.github.jinganix.guess.service.module.auth;

import static io.github.jinganix.guess.service.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.guess.service.module.auth.model.UserToken;
import io.github.jinganix.guess.service.module.user.model.User;
import io.github.jinganix.guess.service.module.user.model.UserStatus;

public class AuthData {

  public static User user() {
    return (User)
        new User()
            .setId(0L)
            .setStatus(UserStatus.ACTIVE)
            .setUpdatedAt(MIN_TIMESTAMP)
            .setCreatedAt(MIN_TIMESTAMP);
  }

  public static UserToken userToken() {
    return (UserToken)
        new UserToken()
            .setId(0L)
            .setUserId(0L)
            .setRefreshToken("")
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
