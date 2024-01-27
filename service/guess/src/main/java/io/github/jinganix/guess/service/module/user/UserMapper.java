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

import io.github.jinganix.guess.proto.internal.auth.WeappUserPb;
import io.github.jinganix.guess.proto.service.user.ConfigPb;
import io.github.jinganix.guess.proto.service.user.UserExtraPb;
import io.github.jinganix.guess.proto.service.user.UserPb;
import io.github.jinganix.guess.service.helper.utils.ConfigProperties;
import io.github.jinganix.guess.service.module.auth.model.UserWeapp;
import io.github.jinganix.guess.service.module.user.model.User;
import io.github.jinganix.guess.service.module.user.model.UserExtra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper for proto messages. */
@Mapper(componentModel = "spring")
public interface UserMapper {

  UserPb mapToPb(User user);

  UserExtraPb mapToPb(UserExtra extra);

  @Mapping(target = "id", source = "userId")
  WeappUserPb mapToPb(UserWeapp userWeapp);

  /**
   * Mapping from {@link ConfigProperties} to {@link ConfigPb}.
   *
   * @param properties {@link ConfigProperties}
   * @return {@link ConfigPb}
   */
  ConfigPb mapToPb(ConfigProperties properties);
}
