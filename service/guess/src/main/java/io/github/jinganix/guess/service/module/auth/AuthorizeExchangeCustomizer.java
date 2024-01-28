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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeExchangeCustomizer
    implements Customizer<
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

  @Value("${spring.mvc.static-path-pattern}")
  private String staticPath;

  @Override
  public void customize(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry) {
    registry.requestMatchers(AuthLoginRequest.WEBPB_PATH).permitAll();
    registry.requestMatchers(AuthTokenRequest.WEBPB_PATH).permitAll();
    registry.requestMatchers(staticPath).permitAll();
    registry.anyRequest().authenticated();
  }
}
