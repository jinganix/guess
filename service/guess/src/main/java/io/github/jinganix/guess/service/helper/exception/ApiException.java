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

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/** Business exception. */
@Getter
public class ApiException extends RuntimeException {

  private final HttpStatusCode status;

  private final ErrorCode code;

  private ApiException(HttpStatusCode httpStatus, ErrorCode errorCode, String message) {
    super(message);
    this.status = httpStatus;
    this.code = errorCode;
  }

  /**
   * Create an exception.
   *
   * @param error {@link ErrorCode}
   * @param message message
   * @return {@link ApiException}
   */
  public static ApiException of(ErrorCode error, String message) {
    return new ApiException(HttpStatus.BAD_REQUEST, error, message);
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link ErrorCode}
   * @return {@link ApiException}
   */
  public static ApiException of(HttpStatusCode status, ErrorCode errorCode) {
    return new ApiException(status, errorCode, null);
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link ErrorCode}
   * @return {@link ApiException}
   */
  public static ApiException of(ErrorCode errorCode) {
    return new ApiException(HttpStatus.BAD_REQUEST, errorCode, null);
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
