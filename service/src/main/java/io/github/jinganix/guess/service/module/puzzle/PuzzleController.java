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

import io.github.jinganix.guess.proto.service.puzzle.PuzzleAnswerRequest;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleAnswerResponse;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleHintRequest;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleHintResponse;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleIncreaseLimitRequest;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleIncreaseLimitResponse;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleRankingRequest;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleRankingResponse;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleRetrieveRequest;
import io.github.jinganix.guess.proto.service.puzzle.PuzzleRetrieveResponse;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.module.puzzle.handler.PuzzleAnswerHandler;
import io.github.jinganix.guess.service.module.puzzle.handler.PuzzleHintHandler;
import io.github.jinganix.guess.service.module.puzzle.handler.PuzzleIncreaseLimitHandler;
import io.github.jinganix.guess.service.module.puzzle.handler.PuzzleRankingHandler;
import io.github.jinganix.guess.service.module.puzzle.handler.PuzzleRetrieveHandler;
import io.github.jinganix.guess.service.setup.argument.annotations.UserId;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller. */
@RestController
@RequiredArgsConstructor
public class PuzzleController {

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final PuzzleAnswerHandler puzzleAnswerHandler;

  private final PuzzleHintHandler puzzleHintHandler;

  private final PuzzleIncreaseLimitHandler puzzleIncreaseLimitHandler;

  private final PuzzleRankingHandler puzzleRankingHandler;

  private final PuzzleRetrieveHandler puzzleRetrieveHandler;

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = PuzzleHintRequest.class)
  public PuzzleHintResponse hint(@UserId Long userId) {
    return puzzleHintHandler.handle(userId);
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public PuzzleAnswerResponse answer(
      @UserId Long userId, @Valid @RequestBody PuzzleAnswerRequest request) {
    return orderedTraceExecutor.supply(userId, () -> puzzleAnswerHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = PuzzleIncreaseLimitRequest.class)
  public PuzzleIncreaseLimitResponse increaseLimit(@UserId Long userId) {
    return orderedTraceExecutor.supply(userId, () -> puzzleIncreaseLimitHandler.handle(userId));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = PuzzleRankingRequest.class)
  public PuzzleRankingResponse ranking() {
    return puzzleRankingHandler.handle();
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping(message = PuzzleRetrieveRequest.class)
  public PuzzleRetrieveResponse retrieve(@UserId Long userId) {
    return puzzleRetrieveHandler.handle(userId);
  }
}
