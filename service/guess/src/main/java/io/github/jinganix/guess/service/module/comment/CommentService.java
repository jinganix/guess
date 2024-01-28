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

package io.github.jinganix.guess.service.module.comment;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.comment.model.CommentAction;
import io.github.jinganix.guess.service.module.comment.model.CommentStatus;
import io.github.jinganix.guess.service.module.comment.repository.CommentActionRepository;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service. */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentActionRepository commentActionRepository;

  private final CommentRepository commentRepository;

  private final UidGenerator uidGenerator;

  public Comment assertFindComment(Long userId, Long commentId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> ApiException.of(ErrorCode.COMMENT_NOT_FOUND));
    if (comment.getStatus() != CommentStatus.ACTIVE) {
      throw ApiException.of(ErrorCode.COMMENT_NOT_FOUND);
    }
    return comment;
  }

  public CommentAction findOrCreateAction(Long userId, Long commentId, long millis) {
    CommentAction action = commentActionRepository.findByUserIdAndCommentId(userId, commentId);
    if (action == null) {
      action =
          (CommentAction)
              new CommentAction()
                  .setId(uidGenerator.nextUid())
                  .setUserId(userId)
                  .setCommentId(commentId)
                  .setReported(false)
                  .setLiked(false)
                  .setCreatedAt(millis)
                  .setUpdatedAt(millis);
    }
    return action;
  }
}
