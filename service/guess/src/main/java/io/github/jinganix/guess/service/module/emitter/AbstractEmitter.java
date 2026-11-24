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

abstract class AbstractEmitter {

  public int order() {
    return 0;
  }

  void commentCreated(Long momentId) {}

  void commentDeleted(Long momentId) {}

  void commentReported(Long momentId, boolean reported) {}

  void momentCreated(Long userId) {}

  /**
   * Moment deletion. {@code wasReported} carries the pre-delete status snapshot so the
   * reported-delete listener keeps its exact condition without receiving the entity.
   */
  void momentDeleted(Long userId, boolean wasReported) {}

  void momentFollowed(Long userId, boolean followed) {}

  void momentReported(Long userId, boolean reported) {}

  void userCreated(Long userId) {}
}
