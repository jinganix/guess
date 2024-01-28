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

package io.github.jinganix.guess.proto.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Mapping between integer value and enumeration. */
public class PbEnumConverter extends StdConverter<Object, Integer> {

  private static final Map<Class<?>, Optional<Method>> enumValueGetterMap =
      new ConcurrentHashMap<>();

  /**
   * Convert an enum to integer value.
   *
   * @param value enum object
   * @return value
   */
  @Override
  public Integer convert(Object value) {
    Optional<Method> optional = getValueGetter(value.getClass());
    if (optional.isPresent()) {
      try {
        return (Integer) optional.get().invoke(value);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static Optional<Method> getValueGetter(Class<?> type) {
    return enumValueGetterMap.computeIfAbsent(
        type, key -> Optional.ofNullable(resolveValueGetter(key)));
  }

  private static Method resolveValueGetter(Class<?> type) {
    for (Method method : type.getDeclaredMethods()) {
      if (!Modifier.isPublic(method.getModifiers())) {
        continue;
      }
      if (method.getReturnType() != Integer.class && method.getReturnType() != int.class) {
        continue;
      }
      if (method.getParameterCount() != 0) {
        continue;
      }
      return method;
    }
    return null;
  }
}
