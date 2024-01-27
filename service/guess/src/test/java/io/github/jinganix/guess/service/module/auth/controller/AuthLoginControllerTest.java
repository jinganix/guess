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

package io.github.jinganix.guess.service.module.auth.controller;

import static io.github.jinganix.guess.service.tests.TestConst.MILLIS;
import static io.github.jinganix.guess.service.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.guess.proto.service.auth.AuthLoginRequest;
import io.github.jinganix.guess.proto.service.auth.AuthTokenResponse;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.auth.token.UserAuthenticationToken;
import io.github.jinganix.guess.service.module.auth.model.GrantedRole;
import io.github.jinganix.guess.service.tests.SpringBootIntegrationTests;
import io.github.jinganix.guess.service.tests.TestHelper;
import io.github.jinganix.guess.service.tests.TestMatcher;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AuthController$login")
class AuthLoginControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when request is invalid")
  class WhenRequestIsInvalid {

    @Nested
    @DisplayName("when code is null")
    class WhenCodeIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new AuthLoginRequest(null))
            .andExpect(status().isBadRequest())
            .andExpect(TestMatcher.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when code length < 1")
    class WhenCodeLengthLessThan1 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new AuthLoginRequest(""))
            .andExpect(status().isBadRequest())
            .andExpect(TestMatcher.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when code length > 40")
    class WhenCodeLengthGreater40 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        String randomString = RandomStringUtils.randomAlphabetic(41);
        testHelper
            .request(UID_1, new AuthLoginRequest(randomString))
            .andExpect(status().isBadRequest())
            .andExpect(TestMatcher.isError(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Nested
    @DisplayName("when authed")
    class WhenAuthed {

      @Test
      @DisplayName("then response token")
      void thenResponseToken() throws Exception {
        when(uidGenerator.nextUid()).thenReturn(UID_1);
        when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
        when(tokenService.generate(any(), any(), anyString())).thenReturn("test_token");
        doReturn(new UserAuthenticationToken(UID_1, Collections.emptySet()))
            .when(weappCodeAuthenticator)
            .authenticate(any());

        testHelper
            .request(UID_1, new AuthLoginRequest("abc"))
            .andExpect(status().isOk())
            .andExpect(
                result ->
                    assertThat(testHelper.deserialize(result, AuthTokenResponse.class))
                        .usingRecursiveComparison()
                        .isEqualTo(
                            new AuthTokenResponse()
                                .setAccessToken("test_token")
                                .setExpiresIn(MILLIS + TimeUnit.MINUTES.toMillis(5))
                                .setRefreshToken("test_uuid")
                                .setTokenType("Bearer")
                                .setScope(GrantedRole.USER.getScope())));
      }
    }
  }
}
