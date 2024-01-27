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

package io.github.jinganix.guess.service.tests;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Mysql jupiter extension. */
public class MysqlExtension implements BeforeAllCallback {

  private static final AtomicBoolean STARTED = new AtomicBoolean(false);

  private static final MysqlContainer container = new MysqlContainer();

  /**
   * Start a container before all tests.
   *
   * @param context {@link ExtensionContext}
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    if (!STARTED.compareAndSet(false, true)) {
      return;
    }
    container
        .withUsername("root")
        .withPassword("root")
        .withInitScript("init_test_container_database.sql")
        .withReuse(true)
        .start();
    System.setProperty("core.url.db-mysql", container.getJdbcUrl());
  }
}
