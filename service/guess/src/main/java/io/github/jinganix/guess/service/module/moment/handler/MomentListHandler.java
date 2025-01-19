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

package io.github.jinganix.guess.service.module.moment.handler;

import static io.github.jinganix.guess.service.helper.Const.PAGE_SIZE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import io.github.jinganix.guess.proto.service.moment.MomentDetailPb;
import io.github.jinganix.guess.proto.service.moment.MomentListRequest;
import io.github.jinganix.guess.proto.service.moment.MomentListResponse;
import io.github.jinganix.guess.service.module.moment.MomentMapper;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.moment.model.MomentStatus;
import io.github.jinganix.guess.service.module.moment.repository.MomentActionRepository;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentListHandler {

  private static final PageRequest PAGE = PageRequest.of(0, PAGE_SIZE, Sort.by(DESC, "createdAt"));

  private final MomentActionRepository momentActionRepository;

  private final MomentMapper momentMapper;

  private final MomentRepository momentRepository;

  public MomentListResponse handle(Long userId, MomentListRequest request) {
    List<Moment> moments =
        switch (request.getCategory()) {
          case ALL -> listAll(request.getId(), request.getCreatedAt());
          case MINE -> listByUserId(userId, request.getId(), request.getCreatedAt());
          case FOLLOWED -> listFollowed(userId, request.getId(), request.getCreatedAt());
        };
    return new MomentListResponse(moments.size() == PAGE_SIZE, toPbs(userId, moments));
  }

  private List<Moment> listAll(Long id, Long createdAt) {
    if (id == null) {
      return momentRepository.findAll(PAGE);
    } else {
      return momentRepository.findAllByCursor(createdAt, id, PAGE);
    }
  }

  private List<Moment> listByUserId(Long userId, Long momentId, Long createdAt) {
    if (momentId == null) {
      return momentRepository.findAllByUserId(userId, PAGE);
    } else {
      return momentRepository.findAllByUserIdAndCursor(userId, createdAt, momentId, PAGE);
    }
  }

  private List<Moment> listFollowed(Long userId, Long momentId, Long createdAt) {
    List<MomentAction> actions;
    if (momentId == null) {
      actions = momentActionRepository.findAllByUserId(userId, PAGE);
    } else {
      MomentAction action = momentActionRepository.findByUserIdAndMomentId(userId, momentId);
      actions =
          momentActionRepository.findAllByUserIdAndCursor(userId, createdAt, action.getId(), PAGE);
    }
    List<Long> momentIds = actions.stream().map(MomentAction::getMomentId).toList();
    List<Moment> moments = new ArrayList<>(PAGE_SIZE);
    momentRepository.findAllById(momentIds).forEach(moments::add);
    moments.sort(Comparator.comparingInt(e -> momentIds.indexOf(e.getId())));
    return moments;
  }

  private List<MomentDetailPb> toPbs(Long userId, List<Moment> moments) {
    Set<Long> momentIds = moments.stream().map(Moment::getId).collect(Collectors.toSet());
    Map<Long, MomentAction> actionMap =
        momentActionRepository.findAllByUserIdAndMomentIdIn(userId, momentIds).stream()
            .collect(Collectors.toMap(MomentAction::getMomentId, x -> x));
    return moments.stream()
        .map(
            moment -> {
              MomentAction action = actionMap.get(moment.getId());
              int answer = Objects.equals(moment.getUserId(), userId) ? moment.getAnswer() : 0;
              MomentDetailPb pb = momentMapper.mapToPb(moment, action, answer);
              if (moment.getStatus() != MomentStatus.ACTIVE) {
                pb.getMoment()
                    .setContent("")
                    .setOption1("")
                    .setOption2("")
                    .setOption3("")
                    .setOption4("");
              }
              return pb;
            })
        .toList();
  }
}
