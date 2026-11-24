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

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.proto.service.error.ErrorMessage;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import org.springframework.test.web.servlet.ResultMatcher;

public class TestMatcher {

  public static ResultMatcher isError(ErrorCode errorCode) {
    return result -> {
      String body = result.getResponse().getContentAsString();
      ErrorMessage message = WebpbUtils.deserialize(body, ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(errorCode);
    };
  }
}
