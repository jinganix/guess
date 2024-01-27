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

package io.github.jinganix.guess.service.module.puzzle.handler;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleAnswerRequest;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleAnswerResponse;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.puzzle.PuzzleService;
import io.github.jinganix.guess.service.module.puzzle.config.PuzzleCfg;
import io.github.jinganix.guess.service.module.puzzle.config.PuzzleCfgService;
import io.github.jinganix.guess.service.module.puzzle.model.PuzzleAction;
import io.github.jinganix.guess.service.module.puzzle.repository.PuzzleActionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PuzzleAnswerHandler {

  private final PuzzleActionRepository puzzleActionRepository;

  private final PuzzleCfgService puzzleCfgService;

  private final PuzzleService puzzleService;

  private final UtilsService utilsService;

  @Transactional
  public PuzzleAnswerResponse handle(Long userId, PuzzleAnswerRequest request) {
    long millis = utilsService.currentTimeMillis();
    PuzzleAction action = puzzleService.findOrCreateAction(userId, millis);
    if (puzzleService.puzzleLimit(action) <= 0) {
      throw ApiException.of(ErrorCode.PERMISSION_DENIED);
    }
    PuzzleCfg cfg = puzzleCfgService.getCfg(action.getLevel() + 1);
    if (cfg == null) {
      throw ApiException.of(ErrorCode.BAD_REQUEST);
    }
    boolean correct = StringUtils.equalsIgnoreCase(cfg.getAnswer(), request.getAnswer());
    if (correct) {
      action
          .setLevel(action.getLevel() + 1)
          .setDailyAttempts(action.getDailyAttempts() + 1)
          .setUpdatedAt(millis);
      puzzleActionRepository.save(action);
    }
    return new PuzzleAnswerResponse(correct);
  }
}
