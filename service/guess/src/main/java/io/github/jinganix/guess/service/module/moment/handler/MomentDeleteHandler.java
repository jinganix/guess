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

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.proto.service.moment.MomentDeleteRequest;
import io.github.jinganix.guess.proto.service.moment.MomentDeleteResponse;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentStatus;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MomentDeleteHandler {

  private final Emitter emitter;

  private final MomentRepository momentRepository;

  private final UtilsService utilsService;

  @Transactional
  public MomentDeleteResponse handle(Long userId, MomentDeleteRequest request) {
    Moment moment =
        momentRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.MOMENT_NOT_FOUND));
    if (!Objects.equals(moment.getUserId(), userId)) {
      throw ApiException.of(ErrorCode.PERMISSION_DENIED);
    }
    long millis = utilsService.currentTimeMillis();
    momentRepository.setStatusById(moment.getId(), MomentStatus.DELETED, millis);
    emitter.momentDeleted(moment);
    return new MomentDeleteResponse();
  }
}
