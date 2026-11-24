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

package io.github.jinganix.guess.service.module.moment.handler;

import io.github.jinganix.guess.proto.service.moment.MomentLikeRequest;
import io.github.jinganix.guess.proto.service.moment.MomentLikeResponse;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.moment.MomentService;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.moment.repository.MomentActionRepository;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MomentLikeHandler {

  private final MomentActionRepository momentActionRepository;

  private final MomentRepository momentRepository;

  private final MomentService momentService;

  private final UtilsService utilsService;

  @Transactional
  public MomentLikeResponse handle(Long userId, MomentLikeRequest request) {
    Long momentId = request.getId();
    momentService.assertFindMoment(momentId);
    long millis = utilsService.currentTimeMillis();
    MomentAction action = momentService.findOrCreateAction(userId, momentId, millis);
    momentActionRepository.save(
        (MomentAction) action.setLiked(!action.isLiked()).setUpdatedAt(millis));
    if (action.isLiked()) {
      momentRepository.incrLike(momentId, millis);
    } else {
      momentRepository.decrLike(momentId, millis);
    }
    return new MomentLikeResponse(action.isLiked());
  }
}
