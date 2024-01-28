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

import io.github.jinganix.guess.proto.service.comment.CommentCreateRequest;
import io.github.jinganix.guess.proto.service.comment.CommentCreateResponse;
import io.github.jinganix.guess.proto.service.comment.CommentDeleteRequest;
import io.github.jinganix.guess.proto.service.comment.CommentDeleteResponse;
import io.github.jinganix.guess.proto.service.comment.CommentLikeRequest;
import io.github.jinganix.guess.proto.service.comment.CommentLikeResponse;
import io.github.jinganix.guess.proto.service.comment.CommentListRequest;
import io.github.jinganix.guess.proto.service.comment.CommentListResponse;
import io.github.jinganix.guess.proto.service.comment.CommentReportRequest;
import io.github.jinganix.guess.proto.service.comment.CommentReportResponse;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.module.comment.handler.CommentCreateHandler;
import io.github.jinganix.guess.service.module.comment.handler.CommentDeleteHandler;
import io.github.jinganix.guess.service.module.comment.handler.CommentLikeHandler;
import io.github.jinganix.guess.service.module.comment.handler.CommentListHandler;
import io.github.jinganix.guess.service.module.comment.handler.CommentReportHandler;
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
public class CommentController {

  private final CommentCreateHandler commentCreateHandler;

  private final CommentDeleteHandler commentDeleteHandler;

  private final CommentLikeHandler commentLikeHandler;

  private final CommentListHandler commentListHandler;

  private final CommentReportHandler commentReportHandler;

  private final OrderedTraceExecutor orderedTraceExecutor;

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public CommentCreateResponse create(
      @UserId Long userId, @Valid @RequestBody CommentCreateRequest request) {
    return orderedTraceExecutor.supply(userId, () -> commentCreateHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public CommentDeleteResponse delete(
      @UserId Long userId, @Valid @RequestBody CommentDeleteRequest request) {
    return orderedTraceExecutor.supply(userId, () -> commentDeleteHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public CommentLikeResponse like(
      @UserId Long userId, @Valid @RequestBody CommentLikeRequest request) {
    return orderedTraceExecutor.supply(userId, () -> commentLikeHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public CommentListResponse list(
      @UserId Long userId, @Valid @RequestBody CommentListRequest request) {
    return commentListHandler.handle(userId, request);
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public CommentReportResponse report(
      @UserId Long userId, @Valid @RequestBody CommentReportRequest request) {
    return orderedTraceExecutor.supply(userId, () -> commentReportHandler.handle(userId, request));
  }
}
