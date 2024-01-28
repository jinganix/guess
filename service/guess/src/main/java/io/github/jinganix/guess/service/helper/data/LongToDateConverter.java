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

package io.github.jinganix.guess.service.helper.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Date;

@Converter
public class LongToDateConverter implements AttributeConverter<Long, Date> {

  @Override
  public Date convertToDatabaseColumn(Long attribute) {
    return attribute == null ? null : new Date(attribute);
  }

  @Override
  public Long convertToEntityAttribute(Date dbData) {
    return dbData == null ? null : dbData.getTime();
  }
}
