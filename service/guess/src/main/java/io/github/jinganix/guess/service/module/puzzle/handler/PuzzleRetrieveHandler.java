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

import io.github.jinganix.guess.proto.service.puzzle.PuzzleRetrieveResponse;
import io.github.jinganix.guess.service.helper.fake.FakeRandom;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.puzzle.PuzzleMapper;
import io.github.jinganix.guess.service.module.puzzle.PuzzleService;
import io.github.jinganix.guess.service.module.puzzle.config.PuzzleCfg;
import io.github.jinganix.guess.service.module.puzzle.config.PuzzleCfgService;
import io.github.jinganix.guess.service.module.puzzle.model.PuzzleAction;
import io.github.jinganix.guess.service.module.puzzle.repository.PuzzleActionRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PuzzleRetrieveHandler {

  private final PuzzleActionRepository puzzleActionRepository;

  private final PuzzleCfgService puzzleCfgService;

  private final PuzzleMapper puzzleMapper;

  private final PuzzleService puzzleService;

  private final UtilsService utilsService;

  @Transactional
  public PuzzleRetrieveResponse handle(Long userId) {
    PuzzleAction action = puzzleActionRepository.findByUserId(userId);
    int level = action == null ? 0 : action.getLevel();
    PuzzleCfg cfg = puzzleCfgService.getCfg(level + 1);
    if (cfg == null) {
      return new PuzzleRetrieveResponse();
    }
    action = puzzleService.checkReset(action, userId, utilsService.currentTimeMillis());
    List<String> options = new ArrayList<>(4);
    int index = FakeRandom.nextInt(cfg.getId() * 10L, 0, 4);
    int counter = 0;
    while (options.size() < 4) {
      if (options.size() == index && !options.contains(cfg.getAnswer())) {
        options.add(cfg.getAnswer());
        continue;
      }
      String option = puzzleCfgService.randomAnswer(cfg.getType(), cfg.getId() * 10L + ++counter);
      if (!options.contains(option)) {
        options.add(option);
      }
    }
    return new PuzzleRetrieveResponse(
        puzzleMapper.mapToPb(cfg, options, puzzleService.puzzleLimit(action)));
  }
}
