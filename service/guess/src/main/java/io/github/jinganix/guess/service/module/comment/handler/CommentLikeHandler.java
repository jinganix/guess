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

import io.github.jinganix.guess.proto.service.comment.CommentLikeRequest;
import io.github.jinganix.guess.proto.service.comment.CommentLikeResponse;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.comment.CommentService;
import io.github.jinganix.guess.service.module.comment.model.CommentAction;
import io.github.jinganix.guess.service.module.comment.repository.CommentActionRepository;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentLikeHandler {

  private final CommentActionRepository commentActionRepository;

  private final CommentRepository commentRepository;

  private final CommentService commentService;

  private final UtilsService utilsService;

  @Transactional
  public CommentLikeResponse handle(Long userId, CommentLikeRequest request) {
    Long commentId = request.getId();
    commentService.assertFindComment(userId, commentId);
    long millis = utilsService.currentTimeMillis();
    CommentAction action = commentService.findOrCreateAction(userId, commentId, millis);
    commentActionRepository.save(
        (CommentAction) action.setLiked(!action.isLiked()).setUpdatedAt(millis));
    if (action.isLiked()) {
      commentRepository.incrLike(commentId, millis);
    } else {
      commentRepository.decrLike(commentId, millis);
    }
    return new CommentLikeResponse(action.isLiked());
  }
}
