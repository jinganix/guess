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

package io.github.jinganix.guess.service.module.user;

import io.github.jinganix.guess.proto.service.user.UserConfigRequest;
import io.github.jinganix.guess.proto.service.user.UserConfigResponse;
import io.github.jinganix.guess.proto.service.user.UserCurrentRequest;
import io.github.jinganix.guess.proto.service.user.UserCurrentResponse;
import io.github.jinganix.guess.proto.service.user.UserExtraRequest;
import io.github.jinganix.guess.proto.service.user.UserExtraResponse;
import io.github.jinganix.guess.proto.service.user.UserListRequest;
import io.github.jinganix.guess.proto.service.user.UserListResponse;
import io.github.jinganix.guess.proto.service.user.UserUpdateRequest;
import io.github.jinganix.guess.proto.service.user.UserUpdateResponse;
import io.github.jinganix.guess.service.module.user.handler.UserConfigHandler;
import io.github.jinganix.guess.service.module.user.handler.UserCurrentHandler;
import io.github.jinganix.guess.service.module.user.handler.UserExtraHandler;
import io.github.jinganix.guess.service.module.user.handler.UserListHandler;
import io.github.jinganix.guess.service.module.user.handler.UserUpdateHandler;
import io.github.jinganix.guess.service.setup.argument.annotations.UserId;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller. */
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserConfigHandler userConfigHandler;

  private final UserCurrentHandler userCurrentHandler;

  private final UserExtraHandler userExtraHandler;

  private final UserListHandler userListHandler;

  private final UserUpdateHandler userUpdateHandler;

  /**
   * Retrieve config.
   *
   * @return {@link UserConfigResponse}
   */
  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = UserConfigRequest.class)
  public UserConfigResponse config() {
    return userConfigHandler.handle();
  }

  /**
   * Retrieve current user.
   *
   * @return {@link UserCurrentResponse}
   */
  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = UserCurrentRequest.class)
  public UserCurrentResponse current(@UserId Long userId) {
    return userCurrentHandler.handle(userId);
  }

  /**
   * Retrieve user extra.
   *
   * @return {@link UserExtraResponse}
   */
  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = UserExtraRequest.class)
  public UserExtraResponse extra(@UserId Long userId) {
    return userExtraHandler.handle(userId);
  }

  /**
   * Retrieve.
   *
   * @param request {@link UserListRequest}
   * @return {@link UserListResponse}
   */
  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public UserListResponse list(@Valid @RequestBody UserListRequest request) {
    return userListHandler.handle(request);
  }

  /**
   * Update user data.
   *
   * @param request {@link UserUpdateRequest}
   * @return {@link UserUpdateResponse}
   */
  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public UserUpdateResponse update(
      @UserId Long userId, @Valid @RequestBody UserUpdateRequest request) {
    return userUpdateHandler.handle(userId, request);
  }
}
