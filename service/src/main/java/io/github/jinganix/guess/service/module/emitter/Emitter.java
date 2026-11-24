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

package io.github.jinganix.guess.service.module.emitter;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class Emitter extends AbstractEmitter {

  private final List<OnCommentCreated> commentCreated;

  private final List<OnCommentDeleted> commentDeleted;

  private final List<OnCommentReported> commentReported;

  private final List<OnMomentCreated> momentCreated;

  private final List<OnMomentDeleted> momentDeleted;

  private final List<OnMomentFollowed> momentFollowed;

  private final List<OnMomentReported> momentReported;

  private final List<OnUserCreated> userCreated;

  @PostConstruct
  @SuppressWarnings("unchecked")
  void initialize() throws IllegalAccessException {
    for (Field field : this.getClass().getDeclaredFields()) {
      if (!List.class.isAssignableFrom(field.getType())) {
        continue;
      }
      Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
      if (!AbstractEmitter.class.isAssignableFrom((Class<?>) type)) {
        continue;
      }
      List<AbstractEmitter> emitters = (List<AbstractEmitter>) field.get(this);
      emitters.sort(Comparator.comparingInt(AbstractEmitter::order));
    }
  }

  private <T extends AbstractEmitter> void emit(List<T> emitters, Consumer<T> func) {
    if (CollectionUtils.isEmpty(emitters)) {
      return;
    }
    for (T emitter : emitters) {
      func.accept(emitter);
    }
  }

  @Override
  public void commentCreated(Long momentId) {
    this.emit(commentCreated, emitter -> emitter.commentCreated(momentId));
  }

  @Override
  public void commentDeleted(Long momentId) {
    this.emit(commentDeleted, emitter -> emitter.commentDeleted(momentId));
  }

  @Override
  public void commentReported(Long momentId, boolean reported) {
    this.emit(commentReported, emitter -> emitter.commentReported(momentId, reported));
  }

  @Override
  public void momentCreated(Long userId) {
    this.emit(momentCreated, emitter -> emitter.momentCreated(userId));
  }

  @Override
  public void momentDeleted(Long userId, boolean wasReported) {
    this.emit(momentDeleted, emitter -> emitter.momentDeleted(userId, wasReported));
  }

  @Override
  public void momentFollowed(Long userId, boolean followed) {
    this.emit(momentFollowed, emitter -> emitter.momentFollowed(userId, followed));
  }

  @Override
  public void momentReported(Long userId, boolean reported) {
    this.emit(momentReported, emitter -> emitter.momentReported(userId, reported));
  }

  @Override
  public void userCreated(Long userId) {
    this.emit(userCreated, emitter -> emitter.userCreated(userId));
  }
}
