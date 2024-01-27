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

package io.github.jinganix.guess.service.setup.exception;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.proto.service.error.ErrorMessage;
import io.github.jinganix.guess.service.helper.exception.ApiException;
import io.github.jinganix.guess.service.helper.exception.BusinessException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/** Global exception handler. */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final ErrorCodeMapper errorCodeMapper;

  /**
   * Format validation exception error message.
   *
   * @param ex {@link MethodArgumentNotValidException}
   * @return map of messages
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorMessage handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return new ErrorMessage(ErrorCode.BAD_REQUEST, null, errors);
  }

  /**
   * Format validation exception error message.
   *
   * @param ex {@link WebExchangeBindException}
   * @return map of messages
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(WebExchangeBindException.class)
  public ErrorMessage handleBindException(WebExchangeBindException ex) {
    log.warn("BAD_REQUEST: {}", ex.getMessage());
    return new ErrorMessage().setCode(ErrorCode.BAD_REQUEST).setMessage(ex.getMessage());
  }

  /**
   * Handle api exception.
   *
   * @param ex {@link ApiException}
   * @return {@link ErrorMessage}
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<?> handleApiException(ApiException ex) {
    return ResponseEntity.status(ex.getStatus())
        .body(new ErrorMessage().setCode(ex.getCode()).setMessage(ex.getMessage()));
  }

  /**
   * Handle business exception.
   *
   * @param ex {@link BusinessException}
   * @return {@link ErrorMessage}
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BusinessException.class)
  public ErrorMessage handleBusinessException(BusinessException ex) {
    return new ErrorMessage()
        .setCode(errorCodeMapper.map(ex.getCode()))
        .setMessage(ex.getMessage());
  }

  /**
   * Handle access denied exception.
   *
   * @param ex {@link AccessDeniedException}
   * @return {@link ErrorMessage}
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public ErrorMessage handleAccessDeniedException(AccessDeniedException ex) {
    return new ErrorMessage(ErrorCode.ERROR, ex.getMessage(), null);
  }

  /**
   * Handle generic exception.
   *
   * @param ex {@link Exception}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler
  public ResponseEntity<?> handleGenericException(Exception ex) {
    log.error("Generic exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorMessage(ErrorCode.ERROR, ex.getMessage(), null));
  }
}
