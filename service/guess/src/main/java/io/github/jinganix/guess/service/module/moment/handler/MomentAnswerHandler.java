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

import io.github.jinganix.guess.proto.service.moment.MomentAnswerRequest;
import io.github.jinganix.guess.proto.service.moment.MomentAnswerResponse;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.moment.MomentService;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.moment.repository.MomentActionRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MomentAnswerHandler {

  private final UtilsService utilsService;

  private final MomentActionRepository momentActionRepository;

  private final MomentService momentService;

  @Transactional
  public MomentAnswerResponse handle(Long userId, MomentAnswerRequest request) {
    Moment moment = momentService.assertFindMoment(request.getId());
    long millis = utilsService.currentTimeMillis();
    MomentAction action = momentActionRepository.findByUserIdAndMomentId(userId, moment.getId());
    if (action == null) {
      action = momentService.findOrCreateAction(userId, moment.getId(), millis);
    }
    boolean correct = Objects.equals(request.getAnswer(), moment.getAnswer());
    action.setCorrect(correct).setUpdatedAt(millis);
    momentActionRepository.save(action);
    return new MomentAnswerResponse(correct);
  }
}
