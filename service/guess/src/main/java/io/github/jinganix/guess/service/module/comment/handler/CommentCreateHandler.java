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

import io.github.jinganix.guess.proto.service.comment.CommentCreateRequest;
import io.github.jinganix.guess.proto.service.comment.CommentCreateResponse;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.comment.CommentMapper;
import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.comment.model.CommentStatus;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import io.github.jinganix.guess.service.module.emitter.Emitter;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import io.github.jinganix.guess.service.module.wechat.WeappProvider;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentCreateHandler {

  private final CommentMapper commentMapper;

  private final CommentRepository commentRepository;

  private final Emitter emitter;

  private final MomentRepository momentRepository;

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  private final WeappProvider weappProvider;

  @Transactional
  public CommentCreateResponse handle(Long userId, CommentCreateRequest request) {
    if (request.getMomentId() == null && request.getCommentId() == null) {
      throw ApiException.of(ErrorCode.BAD_REQUEST);
    }
    long millis = utilsService.currentTimeMillis();
    orderedTraceExecutor.executeSync(
        userId,
        () ->
            commentRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .ifPresent(
                    comment -> {
                      if (comment.getCreatedAt() > millis - TimeUnit.SECONDS.toMillis(15)) {
                        throw ApiException.of(ErrorCode.OPERATION_TOO_FREQUENT);
                      }
                    }));
    if (!weappProvider.checkContent(request.getContent())) {
      throw ApiException.of(ErrorCode.WEAPP_INVALID_CONTENT);
    }
    Long toUserId;
    Long momentId;
    if (request.getCommentId() != null) {
      Comment comment =
          commentRepository
              .findById(request.getCommentId())
              .orElseThrow(() -> ApiException.of(ErrorCode.COMMENT_NOT_FOUND));
      toUserId = comment.getUserId();
      momentId = comment.getMomentId();
    } else {
      Moment moment =
          momentRepository
              .findById(request.getMomentId())
              .orElseThrow(() -> ApiException.of(ErrorCode.MOMENT_NOT_FOUND));
      toUserId = null;
      momentId = moment.getId();
    }
    Comment comment =
        (Comment)
            new Comment()
                .setId(uidGenerator.nextUid())
                .setMomentId(momentId)
                .setUserId(userId)
                .setToUserId(toUserId)
                .setContent(request.getContent())
                .setLike(0)
                .setReport(0)
                .setStatus(CommentStatus.ACTIVE)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    return orderedTraceExecutor.supply(
        comment.getMomentId(),
        () -> {
          commentRepository.save(comment);
          emitter.commentCreated(comment);
          return new CommentCreateResponse(commentMapper.mapToPb(comment, null));
        });
  }
}
