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

import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import io.github.jinganix.guess.service.module.user.model.User;

abstract class AbstractEmitter {

  public int order() {
    return 0;
  }

  void commentCreated(Comment comment) {}

  void commentDeleted(Comment comment) {}

  void commentReported(Comment comment) {}

  void momentCreated(Moment moment) {}

  void momentDeleted(Moment moment) {}

  void momentFollowed(MomentAction action) {}

  void momentReported(Moment moment) {}

  void userCreated(User user) {}
}
