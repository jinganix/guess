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

/** Snowflake generator. */
public class Snowflake {

  private static final long SEQ_MAGIC = 1021;

  private static final long START_STAMP = 1577808000000L;

  private static final long SEQUENCE_BIT = 12;

  private static final long MACHINE_BIT = 10;

  private static final long DATACENTER_BIT = 0;

  private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

  private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

  private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

  private static final long MACHINE_LEFT = SEQUENCE_BIT;

  private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

  private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

  private final long datacenterId;

  private final long machineId;

  private long sequence = 0L;

  private long lastStamp = -1L;

  /**
   * Constructor.
   *
   * @param datacenterId data center Id
   * @param machineId machined Id
   */
  public Snowflake(long datacenterId, long machineId) {
    if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
      throw new IllegalArgumentException(
          "datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
    }
    if (machineId > MAX_MACHINE_NUM || machineId < 0) {
      throw new IllegalArgumentException(
          "machineId can't be greater than MAX_MACHINE_NUM or less than 0");
    }
    this.datacenterId = datacenterId;
    this.machineId = machineId;
  }

  /**
   * Generate next uid.
   *
   * @return next uid
   */
  public synchronized long nextId() {
    long currStamp = getMillis();
    if (currStamp < lastStamp) {
      throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
    }

    if (currStamp == lastStamp) {
      sequence = (sequence + 1) & MAX_SEQUENCE;
      if (sequence == currStamp % SEQ_MAGIC) {
        currStamp = getNextMillis();
      }
    } else {
      sequence = currStamp % SEQ_MAGIC;
    }

    lastStamp = currStamp;
    return (currStamp - START_STAMP) << TIMESTAMP_LEFT
        | datacenterId << DATACENTER_LEFT
        | machineId << MACHINE_LEFT
        | sequence;
  }

  private long getNextMillis() {
    long millis = getMillis();
    while (millis <= lastStamp) {
      millis = getMillis();
    }
    return millis;
  }

  /**
   * Get currentTimeMillis.
   *
   * @return current millis
   */
  public long getMillis() {
    return System.currentTimeMillis();
  }
}
