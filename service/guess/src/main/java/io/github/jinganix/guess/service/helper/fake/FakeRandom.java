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

package io.github.jinganix.guess.service.helper.fake;

public class FakeRandom {

  public static final String BAD_RANGE = "bound must be greater than origin";

  public static int nextInt(long seed) {
    return (int) Math.abs(Hash.hash(seed));
  }

  public static int nextInt(long seed, int origin, int bound) {
    if (origin >= bound) {
      throw new IllegalArgumentException(BAD_RANGE);
    }
    return origin + Math.abs(Hash.hash((int) seed)) % (bound - origin);
  }

  public static long nextLong(long seed) {
    return Math.abs(Hash.hash(seed));
  }

  public static long nextLong(long seed, long origin, long bound) {
    if (origin >= bound) {
      throw new IllegalArgumentException(BAD_RANGE);
    }
    return origin + Math.abs(Hash.hash(seed)) % (bound - origin);
  }

  public static double nextDouble(long seed) {
    return (double) Hash.hash(seed) / Long.MAX_VALUE;
  }

  public static double nextDouble(long seed, double origin, double bound) {
    if (origin >= bound) {
      throw new IllegalArgumentException(BAD_RANGE);
    }
    return origin + ((double) Hash.hash(seed) / Long.MAX_VALUE) % (bound - origin);
  }
}
