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

package io.github.jinganix.guess.service.module.moment;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.moment.model.MomentStatus;
import io.github.jinganix.guess.service.module.moment.repository.MomentActionRepository;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {

  private final MomentActionRepository momentActionRepository;

  private final MomentRepository momentRepository;

  private final UidGenerator uidGenerator;

  public Moment assertFindMoment(Long momentId) {
    Moment moment =
        momentRepository
            .findById(momentId)
            .orElseThrow(() -> ApiException.of(ErrorCode.MOMENT_NOT_FOUND));
    if (moment.getStatus() != MomentStatus.ACTIVE) {
      throw ApiException.of(ErrorCode.MOMENT_NOT_FOUND);
    }
    return moment;
  }

  public MomentAction findOrCreateAction(Long userId, Long momentId, long millis) {
    MomentAction action = momentActionRepository.findByUserIdAndMomentId(userId, momentId);
    if (action == null) {
      action =
          (MomentAction)
              new MomentAction()
                  .setId(uidGenerator.nextUid())
                  .setUserId(userId)
                  .setMomentId(momentId)
                  .setCorrect(false)
                  .setReported(false)
                  .setFollowed(false)
                  .setLiked(false)
                  .setCreatedAt(millis)
                  .setUpdatedAt(millis);
    }
    return action;
  }
}
