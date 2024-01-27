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

import io.github.jinganix.guess.proto.service.auth.AuthTokenResponse;
import io.github.jinganix.guess.service.helper.auth.token.TokenService;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.auth.model.GrantedRole;
import io.github.jinganix.guess.service.module.auth.model.UserToken;
import io.github.jinganix.guess.service.module.auth.repository.UserTokenRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service. */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final TokenService tokenService;

  private final UserTokenRepository userTokenRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  public AuthTokenResponse createAuthTokenResponse(Long userId) {
    GrantedRole grantedRole = GrantedRole.USER;
    String accessToken =
        tokenService.generate(userId, utilsService.uuid(true), grantedRole.getScope());
    String refreshToken = utilsService.uuid(true);

    long millis = utilsService.currentTimeMillis();
    UserToken token =
        (UserToken)
            new UserToken()
                .setId(uidGenerator.nextUid())
                .setRefreshToken(refreshToken)
                .setUserId(userId)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    userTokenRepository.save(token);
    Long expiresIn = millis + TimeUnit.MINUTES.toMillis(5);
    return new AuthTokenResponse(
        accessToken, expiresIn, refreshToken, "Bearer", grantedRole.getScope());
  }
}
