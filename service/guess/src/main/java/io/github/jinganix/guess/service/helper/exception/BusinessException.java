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

package io.github.jinganix.guess.service.helper.exception;

import lombok.Getter;

/** Business exception. */
@Getter
public class BusinessException extends RuntimeException {

  private final BusinessErrorCode code;

  private BusinessException(BusinessErrorCode errorCode, String message) {
    super(message);
    this.code = errorCode;
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link BusinessErrorCode}
   * @return {@link BusinessException}
   */
  public static BusinessException of(BusinessErrorCode errorCode) {
    return new BusinessException(errorCode, null);
  }

  /**
   * Not fill the stacktrace.
   *
   * @return {@link Throwable}
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
