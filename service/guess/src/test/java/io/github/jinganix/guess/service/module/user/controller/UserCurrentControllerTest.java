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

package io.github.jinganix.guess.service.module.user.controller;

import static io.github.jinganix.guess.service.module.auth.AuthData.user;
import static io.github.jinganix.guess.service.tests.TestConst.UID_1;
import static io.github.jinganix.guess.service.tests.TestMatcher.isError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.proto.service.user.UserCurrentRequest;
import io.github.jinganix.guess.proto.service.user.UserCurrentResponse;
import io.github.jinganix.guess.proto.service.user.UserPb;
import io.github.jinganix.guess.service.tests.SpringBootIntegrationTests;
import io.github.jinganix.guess.service.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserController$current")
class UserCurrentControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when check fails")
  class WhenCheckFails {

    @Nested
    @DisplayName("when user not found")
    class WhenUserNotFound {

      @Test
      @DisplayName("then response error")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new UserCurrentRequest())
            .andExpect(status().isBadRequest())
            .andExpect(isError(ErrorCode.USER_NOT_FOUND));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Nested
    @DisplayName("when created")
    class WhenCreated {

      @Test
      @DisplayName("then response")
      void thenResponse() throws Exception {
        testHelper.insertEntities(
            user().setId(UID_1).setAvatar("avatar").setNickname("nickname").setGender(1));

        testHelper
            .request(UID_1, new UserCurrentRequest())
            .andExpect(status().isOk())
            .andExpect(
                result ->
                    assertThat(testHelper.deserialize(result, UserCurrentResponse.class))
                        .usingRecursiveComparison()
                        .isEqualTo(
                            new UserCurrentResponse()
                                .setUser(
                                    new UserPb()
                                        .setId(UID_1)
                                        .setAvatar("avatar")
                                        .setGender(1)
                                        .setNickname("nickname"))));
      }
    }
  }
}
