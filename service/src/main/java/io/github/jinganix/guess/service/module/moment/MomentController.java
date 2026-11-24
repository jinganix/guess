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

package io.github.jinganix.guess.service.module.moment;

import io.github.jinganix.guess.proto.service.moment.MomentAnswerRequest;
import io.github.jinganix.guess.proto.service.moment.MomentAnswerResponse;
import io.github.jinganix.guess.proto.service.moment.MomentCreateRequest;
import io.github.jinganix.guess.proto.service.moment.MomentCreateResponse;
import io.github.jinganix.guess.proto.service.moment.MomentDeleteRequest;
import io.github.jinganix.guess.proto.service.moment.MomentDeleteResponse;
import io.github.jinganix.guess.proto.service.moment.MomentFollowRequest;
import io.github.jinganix.guess.proto.service.moment.MomentFollowResponse;
import io.github.jinganix.guess.proto.service.moment.MomentLikeRequest;
import io.github.jinganix.guess.proto.service.moment.MomentLikeResponse;
import io.github.jinganix.guess.proto.service.moment.MomentListRequest;
import io.github.jinganix.guess.proto.service.moment.MomentListResponse;
import io.github.jinganix.guess.proto.service.moment.MomentReportRequest;
import io.github.jinganix.guess.proto.service.moment.MomentReportResponse;
import io.github.jinganix.guess.proto.service.moment.MomentRetrieveRequest;
import io.github.jinganix.guess.proto.service.moment.MomentRetrieveResponse;
import io.github.jinganix.guess.service.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.guess.service.module.moment.handler.MomentAnswerHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentCreateHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentDeleteHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentFollowHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentLikeHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentListHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentReportHandler;
import io.github.jinganix.guess.service.module.moment.handler.MomentRetrieveHandler;
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
public class MomentController {

  private final MomentAnswerHandler momentAnswerHandler;

  private final MomentCreateHandler momentCreateHandler;

  private final MomentDeleteHandler momentDeleteHandler;

  private final MomentFollowHandler momentFollowHandler;

  private final MomentLikeHandler momentLikeHandler;

  private final MomentListHandler momentListHandler;

  private final MomentReportHandler momentReportHandler;

  private final MomentRetrieveHandler momentRetrieveHandler;

  private final OrderedTraceExecutor orderedTraceExecutor;

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentAnswerResponse answer(
      @UserId Long userId, @Valid @RequestBody MomentAnswerRequest request) {
    return orderedTraceExecutor.supply(userId, () -> momentAnswerHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentCreateResponse create(
      @UserId Long userId, @Valid @RequestBody MomentCreateRequest request) {
    return orderedTraceExecutor.supply(userId, () -> momentCreateHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentDeleteResponse delete(
      @UserId Long userId, @Valid @RequestBody MomentDeleteRequest request) {
    return orderedTraceExecutor.supply(userId, () -> momentDeleteHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentFollowResponse follow(
      @UserId Long userId, @Valid @RequestBody MomentFollowRequest request) {
    return orderedTraceExecutor.supply(userId, () -> momentFollowHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentLikeResponse like(
      @UserId Long userId, @Valid @RequestBody MomentLikeRequest request) {
    return orderedTraceExecutor.supply(userId, () -> momentLikeHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentListResponse list(
      @UserId Long userId, @Valid @RequestBody MomentListRequest request) {
    return momentListHandler.handle(userId, request);
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentReportResponse report(
      @UserId Long userId, @Valid @RequestBody MomentReportRequest request) {
    return orderedTraceExecutor.supply(userId, () -> momentReportHandler.handle(userId, request));
  }

  @PreAuthorize("hasRole('user')")
  @WebpbRequestMapping
  public MomentRetrieveResponse retrieve(
      @UserId Long userId, @Valid MomentRetrieveRequest request) {
    return momentRetrieveHandler.handle(userId, request);
  }
}
