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

import static org.springframework.data.domain.Sort.Direction.DESC;

import io.github.jinganix.guess.proto.service.puzzle.PuzzleRankPb;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleRankingResponse;
import io.github.jinganix.guess.service.module.puzzle.model.PuzzleAction;
import io.github.jinganix.guess.service.module.puzzle.repository.PuzzleActionRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PuzzleRankingHandler {

  private static final Pageable TOP_20_PAGEABLE = PageRequest.of(0, 20, DESC, "level", "createdAt");

  private final PuzzleActionRepository puzzleActionRepository;

  public PuzzleRankingResponse handle() {
    List<PuzzleAction> actions = puzzleActionRepository.findAll(TOP_20_PAGEABLE);
    int rank = 1;
    List<PuzzleRankPb> pbs = new ArrayList<>();
    for (PuzzleAction action : actions) {
      pbs.add(new PuzzleRankPb(action.getUserId(), rank++, action.getLevel()));
    }
    return new PuzzleRankingResponse(pbs);
  }
}
