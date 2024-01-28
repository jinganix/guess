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

package io.github.jinganix.guess.service.module.puzzle;

import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.helper.utils.ConfigProperties;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.puzzle.model.PuzzleAction;
import io.github.jinganix.guess.service.module.puzzle.repository.PuzzleActionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** User service. */
@Slf4j
@Service
@RequiredArgsConstructor
public class PuzzleService {

  private final ConfigProperties config;

  private final PuzzleActionRepository puzzleActionRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  public Integer puzzleLimit(PuzzleAction puzzle) {
    if (puzzle == null) {
      return config.getPuzzleDailyLimit();
    }
    return config.getPuzzleDailyLimit()
        + puzzle.getIncreaseTimes() * config.getPuzzleLimitIncrease()
        - puzzle.getDailyAttempts();
  }

  public PuzzleAction findOrCreateAction(Long userId, long millis) {
    PuzzleAction action = puzzleActionRepository.findByUserId(userId);
    return checkReset(action, userId, millis);
  }

  public PuzzleAction checkReset(PuzzleAction action, Long userId, long millis) {
    if (action == null) {
      action =
          (PuzzleAction)
              new PuzzleAction()
                  .setId(uidGenerator.nextUid())
                  .setUserId(userId)
                  .setLevel(0)
                  .setIncreaseTimes(0)
                  .setDailyAttempts(0)
                  .setResetAt(millis)
                  .setCreatedAt(millis)
                  .setUpdatedAt(millis);
    }
    if (!utilsService.isToday(action.getResetAt())) {
      action.setDailyAttempts(0).setIncreaseTimes(0).setResetAt(millis).setUpdatedAt(millis);
    }
    return action;
  }
}
