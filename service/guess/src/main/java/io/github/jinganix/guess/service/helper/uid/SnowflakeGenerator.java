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

package io.github.jinganix.guess.service.helper.uid;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/** UidGeneratorImpl. */
@Slf4j
@RequiredArgsConstructor
public class SnowflakeGenerator implements UidGenerator {

  @Value("${utils.node-id:1}")
  private long nodeId;

  private Snowflake snowflake;

  /** Initialize. */
  @PostConstruct
  public void initialize() {
    this.snowflake = new Snowflake(0, nodeId);
  }

  /**
   * Next id.
   *
   * @return id
   */
  @Override
  public long nextUid() {
    return snowflake.nextId();
  }
}
