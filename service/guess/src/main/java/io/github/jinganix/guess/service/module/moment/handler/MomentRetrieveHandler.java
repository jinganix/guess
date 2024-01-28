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

import io.github.jinganix.guess.proto.service.moment.MomentRetrieveRequest;
import io.github.jinganix.guess.proto.service.moment.MomentRetrieveResponse;
import io.github.jinganix.guess.service.module.moment.MomentMapper;
import io.github.jinganix.guess.service.module.moment.MomentService;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.moment.repository.MomentActionRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentRetrieveHandler {

  private final MomentActionRepository momentActionRepository;

  private final MomentMapper momentMapper;

  private final MomentService momentService;

  public MomentRetrieveResponse handle(Long userId, MomentRetrieveRequest request) {
    Moment moment = momentService.assertFindMoment(request.getId());
    MomentAction action = momentActionRepository.findById(moment.getId()).orElse(null);
    int answer = Objects.equals(moment.getUserId(), userId) ? moment.getAnswer() : 0;
    return new MomentRetrieveResponse(momentMapper.mapToPb(moment, action, answer));
  }
}
