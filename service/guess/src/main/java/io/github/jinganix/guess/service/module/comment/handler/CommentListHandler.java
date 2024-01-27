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

import static io.github.jinganix.guess.service.helper.Const.PAGE_SIZE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import io.github.jinganix.guess.proto.service.comment.CommentListRequest;
import io.github.jinganix.guess.proto.service.comment.CommentListResponse;
import io.github.jinganix.guess.service.module.comment.CommentMapper;
import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.comment.model.CommentAction;
import io.github.jinganix.guess.service.module.comment.repository.CommentActionRepository;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentListHandler {

  private static final PageRequest PAGE = PageRequest.of(0, PAGE_SIZE, Sort.by(DESC, "createdAt"));

  private final CommentActionRepository commentActionRepository;

  private final CommentMapper commentMapper;

  private final CommentRepository commentRepository;

  public CommentListResponse handle(Long userId, CommentListRequest request) {
    List<Comment> comments;
    if (request.getId() == null) {
      comments = commentRepository.findAll(request.getMomentId(), PAGE);
    } else {
      comments =
          commentRepository.findAllByCursor(
              request.getMomentId(), request.getCreatedAt(), request.getId(), PAGE);
    }

    Set<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toSet());
    Map<Long, CommentAction> actionMap =
        commentActionRepository.findByUserIdAndCommentIdIn(userId, commentIds).stream()
            .collect(Collectors.toMap(CommentAction::getCommentId, x -> x));

    return new CommentListResponse(
        comments.size() == PAGE_SIZE,
        comments.stream().map(x -> commentMapper.mapToPb(x, actionMap.get(x.getId()))).toList());
  }
}
