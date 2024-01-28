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

import io.github.jinganix.peashooter.DefaultExecutorSelector;
import io.github.jinganix.peashooter.TaskQueueProvider;
import io.github.jinganix.peashooter.TraceExecutor;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderedTraceExecutor extends io.github.jinganix.peashooter.OrderedTraceExecutor {

  public OrderedTraceExecutor(TaskQueueProvider queueProvider, TraceExecutor executor) {
    super(queueProvider, new DefaultExecutorSelector(executor), executor.getTracer());
  }

  public <R> R supply(Object key, Supplier<R> supplier) {
    return supply(String.valueOf(key), supplier);
  }

  public void executeAsync(Object key, Runnable task) {
    executeAsync(String.valueOf(key), task);
  }

  public void executeSync(Object key, Runnable task) {
    executeSync(String.valueOf(key), task);
  }
}
