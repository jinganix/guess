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

package io.github.jinganix.guess.service.module.utils;

import io.github.jinganix.guess.service.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.guess.service.helper.exception.BusinessErrorCode;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;

public enum ErrorCode implements BusinessErrorCode, Enumeration<Integer> {
  OK;

  private static final IntegerEnumMapper<ErrorCode> mapper = new IntegerEnumMapper<>(values());

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link ErrorCode}
   */
  public static ErrorCode fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get enumeration value.
   *
   * @return {@link Integer}
   */
  @Override
  public Integer getValue() {
    return ordinal();
  }
}
