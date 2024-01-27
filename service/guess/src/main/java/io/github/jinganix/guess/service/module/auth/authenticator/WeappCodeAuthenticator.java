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

package io.github.jinganix.guess.service.module.auth.authenticator;

import io.github.jinganix.guess.proto.internal.weapp.WeappSessionRequest;
import io.github.jinganix.guess.proto.internal.weapp.WeappSessionResponse;
import io.github.jinganix.guess.service.helper.auth.authenticator.Authenticator;
import io.github.jinganix.guess.service.helper.auth.token.UserAuthenticationToken;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.auth.model.UserWeapp;
import io.github.jinganix.guess.service.module.auth.model.WeappCodeToken;
import io.github.jinganix.guess.service.module.auth.repository.UserWeappRepository;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import io.github.jinganix.guess.service.module.user.model.User;
import io.github.jinganix.guess.service.module.user.model.UserStatus;
import io.github.jinganix.guess.service.module.user.repository.UserRepository;
import io.github.jinganix.webpb.runtime.reactive.WebpbClient;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class WeappCodeAuthenticator implements Authenticator {

  @Value("${core.weapp.app-id}")
  private final String appId;

  @Value("${core.weapp.app-secret}")
  private final String appSecret;

  private final Emitter emitter;

  private final UidGenerator uidGenerator;

  private final UserRepository userRepository;

  private final UserWeappRepository userWeappRepository;

  private final UtilsService utilsService;

  private final WebpbClient webpbClient;

  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof WeappCodeToken;
  }

  @Override
  @Transactional
  public UserAuthenticationToken authenticate(Authentication authentication) {
    String code = (String) authentication.getPrincipal();
    WeappSessionRequest request = new WeappSessionRequest(appId, appSecret, code);
    WeappSessionResponse response = webpbClient.request(request, WeappSessionResponse.class);
    if (response.getErrCode() != null && response.getErrCode() != 0) {
      throw new BadCredentialsException("WEAPP_LOGIN_FAILED");
    }
    UserWeapp userWeapp;
    if (StringUtils.hasText(response.getUnionId())) {
      userWeapp = userWeappRepository.findByAppIdAndUnionId(appId, response.getUnionId());
    } else {
      userWeapp = userWeappRepository.findByAppIdAndOpenId(appId, response.getOpenId());
    }

    if (userWeapp == null) {
      long userId = uidGenerator.nextUid();
      User user = createUser(userId);
      userWeapp = createUserWeapp(userId, response);

      userRepository.save(user);
      userWeappRepository.save(userWeapp);
      emitter.userCreated(user);
    } else if (!Objects.equals(userWeapp.getAppId(), appId)) {
      long userId = userWeapp.getUserId();
      User user = createUser(userId);
      userRepository.save(user);
    } else {
      userWeapp.setSessionKey(response.getSessionKey()).setUpdatedAt(System.currentTimeMillis());
      userWeappRepository.save(userWeapp);
    }
    return new UserAuthenticationToken(
        userWeapp.getUserId(), Set.of(new SimpleGrantedAuthority("WEAPP")));
  }

  private User createUser(long userId) {
    long millis = utilsService.currentTimeMillis();
    return (User)
        new User()
            .setId(userId)
            .setStatus(UserStatus.ACTIVE)
            .setCreatedAt(millis)
            .setUpdatedAt(millis);
  }

  private UserWeapp createUserWeapp(long userId, WeappSessionResponse response) {
    long millis = utilsService.currentTimeMillis();
    return (UserWeapp)
        new UserWeapp()
            .setId(uidGenerator.nextUid())
            .setAppId(appId)
            .setOpenId(response.getOpenId())
            .setUnionId(response.getUnionId())
            .setSessionKey(response.getSessionKey())
            .setUserId(userId)
            .setCreatedAt(millis)
            .setUpdatedAt(millis);
  }
}
