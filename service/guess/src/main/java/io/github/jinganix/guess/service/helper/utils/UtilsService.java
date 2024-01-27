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

package io.github.jinganix.guess.service.helper.utils;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class UtilsService {

  public static final long OFFSET_MILLIS = TimeZone.getDefault().getRawOffset();

  public static ZoneId ZONE_ID = ZoneId.systemDefault();

  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  public boolean isToday(long millis) {
    return toLocalDate(currentTimeMillis()).equals(toLocalDate(millis));
  }

  public LocalDate nowDate() {
    return LocalDate.now();
  }

  public LocalDateTime nowDatetime() {
    return LocalDateTime.now();
  }

  public LocalDate toLocalDate(long millis) {
    return Instant.ofEpochMilli(millis).atZone(ZONE_ID).toLocalDate();
  }

  public boolean isSameDay(long a, long b) {
    return toLocalDate(a).equals(toLocalDate(b));
  }

  public <T> T getOneOrLast(List<T> values, int index) {
    return index >= values.size() ? values.get(values.size() - 1) : values.get(index);
  }

  public static int getDays(long time) {
    return (int) TimeUnit.MILLISECONDS.toDays(time + OFFSET_MILLIS);
  }

  public int getDiffDays(long millisA, long millisB) {
    return Math.abs(getDays(millisA) - getDays(millisB));
  }

  public int daysBetween(LocalDate dateA, LocalDate dateB) {
    return (int) DAYS.between(dateA, dateB);
  }

  public long toMillis(LocalDateTime dateTime) {
    return dateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
  }

  public long toMillis(LocalDate date) {
    return date.atStartOfDay(ZONE_ID).toInstant().toEpochMilli();
  }

  /**
   * Generate uuid.
   *
   * @param dash true with dash
   * @return uuid
   */
  public synchronized String uuid(boolean dash) {
    if (dash) {
      return UUID.randomUUID().toString();
    } else {
      return UUID.randomUUID().toString().replace("-", "");
    }
  }
}
