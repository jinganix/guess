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

package io.github.jinganix.guess.service.helper.enumeration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Mapper for enum and value.
 *
 * @param <K> value
 * @param <V> enum
 */
public class EnumMapper<K, V> {

  private final Map<K, V> valueMap = new HashMap<>();

  /**
   * Constructor.
   *
   * @param values values
   * @param keyResolver key resolver
   */
  public EnumMapper(V[] values, Function<V, K> keyResolver) {
    for (V value : values) {
      valueMap.put(keyResolver.apply(value), value);
    }
  }

  /**
   * Enum from value.
   *
   * @param value value
   * @return {@link V}
   */
  public V fromValue(K value) {
    return valueMap.get(value);
  }
}
