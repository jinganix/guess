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

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Start a mysql docker container. */
public class MysqlContainer extends MySQLContainer<MysqlContainer> {

  /** Constructor. */
  public MysqlContainer() {
    super(
        DockerImageName.parse(isArm64() ? "arm64v8/mysql:8.2.0" : "mysql:8.2.0")
            .asCompatibleSubstituteFor("mysql"));
  }

  private static boolean isArm64() {
    return System.getProperty("os.arch").equals("aarch64");
  }
}
