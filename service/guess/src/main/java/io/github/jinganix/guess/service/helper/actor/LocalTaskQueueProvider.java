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

package io.github.jinganix.guess.service.helper.actor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.jinganix.peashooter.LockableTaskQueue;
import io.github.jinganix.peashooter.TaskQueue;
import io.github.jinganix.peashooter.TaskQueueProvider;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Implement {@link LockableTaskQueue} to provide distributed lock queues. */
@Slf4j
@Component
public class LocalTaskQueueProvider implements TaskQueueProvider {

  private final LoadingCache<String, TaskQueue> queues =
      Caffeine.newBuilder()
          .maximumSize(100000)
          .expireAfterAccess(5, TimeUnit.MINUTES)
          .build(_x -> new TaskQueue());

  @Override
  public void remove(String s) {
    this.queues.invalidate(s);
  }

  @Override
  public TaskQueue get(String s) {
    return queues.get(s);
  }
}
