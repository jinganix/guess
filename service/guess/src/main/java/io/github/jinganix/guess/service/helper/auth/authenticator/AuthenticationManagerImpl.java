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

package io.github.jinganix.guess.service.helper.auth.authenticator;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationManagerImpl implements AuthenticationManager {

  private final List<Authenticator> authenticators;

  public AuthenticationManagerImpl(List<Authenticator> authenticators) {
    this.authenticators = authenticators;
  }

  @Override
  public Authentication authenticate(Authentication authenticationToken) {
    for (Authenticator authenticator : authenticators) {
      if (authenticator.support(authenticationToken)) {
        return authenticator.authenticate(authenticationToken);
      }
    }
    throw new RuntimeException("Unhandled authentication: " + authenticationToken.getClass());
  }
}
