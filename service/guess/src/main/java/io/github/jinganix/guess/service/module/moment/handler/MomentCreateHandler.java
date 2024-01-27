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
import io.github.jinganix.guess.proto.service.moment.MomentCreateRequest;
import io.github.jinganix.guess.proto.service.moment.MomentCreateResponse;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import io.github.jinganix.guess.service.module.moment.MomentMapper;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentStatus;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import io.github.jinganix.guess.service.module.wechat.WeappProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MomentCreateHandler {

  private final Emitter emitter;

  private final MomentMapper momentMapper;

  private final MomentRepository momentRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  private final WeappProvider weappProvider;

  @Transactional
  public MomentCreateResponse handle(Long userId, MomentCreateRequest request) {
    long millis = utilsService.currentTimeMillis();
    momentRepository
        .findFirstByUserIdOrderByCreatedAtDesc(userId)
        .ifPresent(
            moment -> {
              if (moment.getCreatedAt() > millis - TimeUnit.SECONDS.toMillis(30)) {
                throw ApiException.of(ErrorCode.OPERATION_TOO_FREQUENT);
              }
            });

    List<String> options =
        new ArrayList<>(
            Arrays.asList(
                request.getOption1(),
                request.getOption2(),
                request.getOption3(),
                request.getOption4()));
    if (options.stream().anyMatch(StringUtils::isNotBlank)) {
      if (options.stream().anyMatch(StringUtils::isBlank)) {
        throw ApiException.of(ErrorCode.BAD_REQUEST);
      }
      if (request.getAnswer() < 1 || request.getAnswer() > 4) {
        throw ApiException.of(ErrorCode.BAD_REQUEST);
      }
    }

    options.add(request.getContent());
    if (!weappProvider.checkTexts(options)) {
      throw ApiException.of(ErrorCode.WEAPP_INVALID_CONTENT);
    }

    Moment moment =
        (Moment)
            new Moment()
                .setId(uidGenerator.nextUid())
                .setUserId(userId)
                .setStatus(MomentStatus.ACTIVE)
                .setAnswer(0)
                .setComment(0)
                .setFollow(0)
                .setLike(0)
                .setReport(0)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    momentMapper.update(moment, request);
    momentRepository.save(moment);
    emitter.momentCreated(moment);
    return new MomentCreateResponse(momentMapper.mapToPb(moment, null, moment.getAnswer()));
  }
}
