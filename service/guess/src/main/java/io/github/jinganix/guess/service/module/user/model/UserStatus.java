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

package io.github.jinganix.guess.service.module.user.model;

import io.github.jinganix.guess.service.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import jakarta.persistence.AttributeConverter;

/** Enum. */
public enum UserStatus implements Enumeration<Integer> {
  ACTIVE(0),
  SUSPENDED(1),
  INACTIVE(2);

  private static final IntegerEnumMapper<UserStatus> mapper = new IntegerEnumMapper<>(values());
  private final int value;

  UserStatus(int value) {
    this.value = value;
  }

  public static UserStatus fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get int value.
   *
   * @return value of the enum
   */
  @Override
  public Integer getValue() {
    return this.value;
  }

  public static class Converter implements AttributeConverter<UserStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserStatus attribute) {
      return attribute == null ? null : attribute.getValue();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer dbData) {
      return UserStatus.fromValue(dbData);
    }
  }
}
