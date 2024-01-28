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

package io.github.jinganix.guess.service.module.comment.handler;

import io.github.jinganix.guess.proto.service.comment.CommentDeleteRequest;
import io.github.jinganix.guess.proto.service.comment.CommentDeleteResponse;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.comment.model.CommentStatus;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentDeleteHandler {

  private final CommentRepository commentRepository;

  private final Emitter emitter;

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final UtilsService utilsService;

  @Transactional
  public CommentDeleteResponse handle(Long userId, CommentDeleteRequest request) {
    Comment comment =
        commentRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.COMMENT_NOT_FOUND));
    if (!Objects.equals(comment.getUserId(), userId)) {
      throw ApiException.of(ErrorCode.PERMISSION_DENIED);
    }
    return orderedTraceExecutor.supply(
        comment.getMomentId(),
        () -> {
          long millis = utilsService.currentTimeMillis();
          commentRepository.setStatusById(comment.getId(), CommentStatus.DELETED, millis);
          emitter.commentDeleted(comment);
          return new CommentDeleteResponse();
        });
  }
}
