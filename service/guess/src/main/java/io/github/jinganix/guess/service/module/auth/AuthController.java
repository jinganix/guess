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

import io.github.jinganix.guess.proto.service.auth.AuthLoginRequest;
import io.github.jinganix.guess.proto.service.auth.AuthTokenRequest;
import io.github.jinganix.guess.proto.service.auth.AuthTokenResponse;
import io.github.jinganix.guess.service.module.auth.handler.AuthLoginHandler;
import io.github.jinganix.guess.service.module.auth.handler.AuthTokenHandler;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller. */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthLoginHandler authLoginHandler;

  private final AuthTokenHandler authTokenHandler;

  @WebpbRequestMapping
  public AuthTokenResponse login(@Valid @RequestBody AuthLoginRequest request) {
    return authLoginHandler.handle(request);
  }

  @WebpbRequestMapping
  public AuthTokenResponse token(@Valid @RequestBody AuthTokenRequest request) {
    return authTokenHandler.handle(request);
  }
}
