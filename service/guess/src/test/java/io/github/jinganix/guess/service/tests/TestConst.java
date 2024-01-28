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

package io.github.jinganix.guess.service.tests;

import java.util.concurrent.TimeUnit;

public interface TestConst {

  Long MIN_TIMESTAMP = TimeUnit.SECONDS.toMillis(1);

  /** MILLIS. */
  long MILLIS = 123456789L;

  /** UID_1. */
  long UID_1 = 10001;

  /** UID_2. */
  long UID_2 = 10002;

  /** UID_3. */
  long UID_3 = 10003;

  /** UID_4. */
  long UID_4 = 10004;

  /** UID_5. */
  long UID_5 = 10005;
}
