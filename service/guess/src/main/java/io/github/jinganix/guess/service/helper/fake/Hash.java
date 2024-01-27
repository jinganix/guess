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

public class Hash {

  private Hash() {}

  public static int hash(int x) {
    x = ((x >>> 16) ^ x) * 0x45d9f3b;
    x = ((x >>> 16) ^ x) * 0x45d9f3b;
    x = (x >>> 16) ^ x;
    return x;
  }

  public static int unhash(int x) {
    x = ((x >>> 16) ^ x) * 0x119de1f3;
    x = ((x >>> 16) ^ x) * 0x119de1f3;
    x = (x >>> 16) ^ x;
    return x;
  }

  public static long hash(long x) {
    x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
    x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
    x = x ^ (x >>> 31);
    return x;
  }

  public static long unhash(long x) {
    x = (x ^ (x >>> 31) ^ (x >>> 62)) * 0x319642b2d24d8ec3L;
    x = (x ^ (x >>> 27) ^ (x >>> 54)) * 0x96de1b173f119089L;
    x = x ^ (x >>> 30) ^ (x >>> 60);
    return x;
  }
}
