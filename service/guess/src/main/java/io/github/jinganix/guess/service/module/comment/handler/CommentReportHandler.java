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

import static io.github.jinganix.guess.service.module.comment.model.CommentStatus.REPORTED;

import io.github.jinganix.guess.proto.service.comment.CommentReportRequest;
import io.github.jinganix.guess.proto.service.comment.CommentReportResponse;
import io.github.jinganix.guess.service.helper.Const;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.comment.CommentService;
import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.comment.model.CommentAction;
import io.github.jinganix.guess.service.module.comment.repository.CommentActionRepository;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentReportHandler {

  private final CommentActionRepository commentActionRepository;

  private final CommentRepository commentRepository;

  private final CommentService commentService;

  private final Emitter emitter;

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final UtilsService utilsService;

  @Transactional
  public CommentReportResponse handle(Long userId, CommentReportRequest request) {
    Long commentId = request.getId();
    Comment comment = commentService.assertFindComment(userId, commentId);
    return orderedTraceExecutor.supply(
        comment.getMomentId(),
        () -> {
          long millis = utilsService.currentTimeMillis();
          CommentAction action = commentService.findOrCreateAction(userId, commentId, millis);
          commentActionRepository.save(
              (CommentAction) action.setReported(!action.isReported()).setUpdatedAt(millis));
          comment
              .setReport(comment.getReport() + (action.isReported() ? 1 : -1))
              .setUpdatedAt(millis);
          if (comment.getReport() >= Const.REPORT_THRESHOLD) {
            comment.setStatus(REPORTED);
          }
          commentRepository.save(comment);
          if (comment.getStatus() == REPORTED) {
            emitter.commentReported(comment);
          }
          return new CommentReportResponse(action.isReported(), comment.getStatus() == REPORTED);
        });
  }
}
