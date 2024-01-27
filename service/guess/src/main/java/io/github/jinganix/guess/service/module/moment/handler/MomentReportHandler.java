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

import static io.github.jinganix.guess.service.module.moment.model.MomentStatus.REPORTED;

import io.github.jinganix.guess.proto.service.moment.MomentReportRequest;
import io.github.jinganix.guess.proto.service.moment.MomentReportResponse;
import io.github.jinganix.guess.service.helper.Const;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import io.github.jinganix.guess.service.module.moment.MomentService;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.moment.repository.MomentActionRepository;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MomentReportHandler {

  private final Emitter emitter;

  private final MomentActionRepository momentActionRepository;

  private final MomentRepository momentRepository;

  private final MomentService momentService;

  private final UtilsService utilsService;

  @Transactional
  public MomentReportResponse handle(Long userId, MomentReportRequest request) {
    Long momentId = request.getId();
    Moment moment = momentService.assertFindMoment(momentId);
    long millis = utilsService.currentTimeMillis();
    MomentAction action = momentService.findOrCreateAction(userId, momentId, millis);
    momentActionRepository.save(
        (MomentAction) action.setReported(!action.isReported()).setUpdatedAt(millis));
    moment.setReport(moment.getReport() + (action.isReported() ? 1 : -1)).setUpdatedAt(millis);
    if (moment.getReport() >= Const.REPORT_THRESHOLD) {
      moment.setStatus(REPORTED);
    }
    momentRepository.save(moment);
    if (moment.getStatus() == REPORTED) {
      emitter.momentReported(moment);
    }
    return new MomentReportResponse(action.isReported(), moment.getStatus() == REPORTED);
  }
}
