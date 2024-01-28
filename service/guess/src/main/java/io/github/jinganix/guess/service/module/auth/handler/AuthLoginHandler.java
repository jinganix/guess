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

import io.github.jinganix.guess.proto.service.auth.AuthLoginRequest;
import io.github.jinganix.guess.proto.service.auth.AuthTokenResponse;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.module.auth.AuthService;
import io.github.jinganix.guess.service.module.auth.model.WeappCodeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthLoginHandler {

  private final AuthenticationManager authenticationManager;

  private final AuthService authService;

  public AuthTokenResponse handle(AuthLoginRequest request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(new WeappCodeToken(request.getCode()));
      Long userId = (Long) authentication.getPrincipal();
      return authService.createAuthTokenResponse(userId);
    } catch (BadCredentialsException ex) {
      throw ApiException.of(ErrorCode.WEAPP_LOGIN_FAILED);
    }
  }
}
