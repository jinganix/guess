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

package io.github.jinganix.guess.service.module.auth.handler;

import io.github.jinganix.guess.proto.service.auth.AuthTokenRequest;
import io.github.jinganix.guess.proto.service.auth.AuthTokenResponse;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.module.auth.AuthService;
import io.github.jinganix.guess.service.module.auth.model.UserToken;
import io.github.jinganix.guess.service.module.auth.repository.UserTokenRepository;
import io.github.jinganix.guess.service.module.user.model.User;
import io.github.jinganix.guess.service.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenHandler {

  private final AuthService authService;

  private final UserTokenRepository userTokenRepository;

  private final UserRepository userRepository;

  public AuthTokenResponse handle(AuthTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    if (StringUtils.isEmpty(refreshToken)) {
      return new AuthTokenResponse();
    }
    UserToken token =
        userTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(
                () -> ApiException.of(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_REFRESH_TOKEN));
    userTokenRepository.deleteById(token.getId());
    User user =
        userRepository
            .findById(token.getUserId())
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    return authService.createAuthTokenResponse(user.getId());
  }
}
