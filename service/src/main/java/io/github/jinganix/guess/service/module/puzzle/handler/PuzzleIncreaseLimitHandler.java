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

import io.github.jinganix.guess.proto.service.puzzle.PuzzleIncreaseLimitResponse;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.puzzle.PuzzleService;
import io.github.jinganix.guess.service.module.puzzle.model.PuzzleAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PuzzleIncreaseLimitHandler {

  private final PuzzleService puzzleService;

  private final UtilsService utilsService;

  @Transactional
  public PuzzleIncreaseLimitResponse handle(Long userId) {
    long millis = utilsService.currentTimeMillis();
    PuzzleAction action = puzzleService.findOrCreateAction(userId, millis);
    action.setIncreaseTimes(action.getIncreaseTimes() + 1).setUpdatedAt(millis);
    return new PuzzleIncreaseLimitResponse(puzzleService.puzzleLimit(action));
  }
}
