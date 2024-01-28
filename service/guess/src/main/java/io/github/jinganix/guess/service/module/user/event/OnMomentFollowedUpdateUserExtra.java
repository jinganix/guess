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

package io.github.jinganix.guess.service.module.user.event;

import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.emitter.OnMomentFollowed;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.user.repository.UserExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnMomentFollowedUpdateUserExtra extends OnMomentFollowed {

  private final UserExtraRepository userExtraRepository;

  private final UtilsService utilsService;

  @Override
  public void momentFollowed(MomentAction action) {
    long millis = utilsService.currentTimeMillis();
    if (action.isFollowed()) {
      userExtraRepository.incrFollow(action.getUserId(), millis);
    } else {
      userExtraRepository.decrFollow(action.getUserId(), millis);
    }
  }
}
