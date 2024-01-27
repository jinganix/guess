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

package io.github.jinganix.guess.service.module.comment.controller;

import static io.github.jinganix.guess.service.module.moment.MomentData.moment;
import static io.github.jinganix.guess.service.tests.TestConst.MILLIS;
import static io.github.jinganix.guess.service.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.guess.proto.service.comment.CommentCreateRequest;
import io.github.jinganix.guess.proto.service.comment.CommentCreateResponse;
import io.github.jinganix.guess.proto.service.comment.CommentFacadePb;
import io.github.jinganix.guess.proto.service.comment.CommentPb;
import io.github.jinganix.guess.proto.service.error.ErrorCode;
import io.github.jinganix.guess.service.module.comment.repository.CommentRepository;
import io.github.jinganix.guess.service.module.moment.repository.MomentRepository;
import io.github.jinganix.guess.service.tests.SpringBootIntegrationTests;
import io.github.jinganix.guess.service.tests.TestHelper;
import io.github.jinganix.guess.service.tests.TestMatcher;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("CommentController$create")
class CommentCreateControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired MomentRepository momentRepository;

  @Autowired CommentRepository commentRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when request is invalid")
  class WhenRequestIsInvalid {

    @Nested
    @DisplayName("when content length < 3")
    class WhenCodeLengthLessThan1 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new CommentCreateRequest(UID_1, null, "aa"))
            .andExpect(status().isBadRequest())
            .andExpect(TestMatcher.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when content length > 140")
    class WhenCodeLengthGreater40 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        String randomString = RandomStringUtils.randomAlphabetic(141);
        testHelper
            .request(UID_1, new CommentCreateRequest(UID_1, null, randomString))
            .andExpect(status().isBadRequest())
            .andExpect(TestMatcher.isError(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when check fails")
  class WhenCheckFails {

    @Nested
    @DisplayName("when moment id and comment id are null")
    class WhenCodeIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new CommentCreateRequest(null, null, "aaaaaa"))
            .andExpect(status().isBadRequest())
            .andExpect(TestMatcher.isError(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Nested
    @DisplayName("when created")
    class WhenCreated {

      @Test
      @DisplayName("then response")
      void thenResponse() throws Exception {
        testHelper.insertEntities(moment().setId(UID_1));

        when(uidGenerator.nextUid()).thenReturn(UID_1);
        when(weappProvider.checkContent(anyString())).thenReturn(true);

        testHelper
            .request(UID_1, new CommentCreateRequest(UID_1, null, "aaaaaa"))
            .andExpect(status().isOk())
            .andExpect(
                result ->
                    assertThat(testHelper.deserialize(result, CommentCreateResponse.class))
                        .usingRecursiveComparison()
                        .isEqualTo(
                            new CommentCreateResponse()
                                .setComment(
                                    new CommentFacadePb()
                                        .setComment(
                                            new CommentPb()
                                                .setId(UID_1)
                                                .setMomentId(UID_1)
                                                .setContent("aaaaaa")
                                                .setLike(0)
                                                .setUserId(UID_1)
                                                .setCreatedAt(MILLIS)))));
        assertThat(commentRepository.findById(UID_1)).isNotEmpty();
        assertThat(momentRepository.findById(UID_1))
            .isNotEmpty()
            .hasValueSatisfying(moment -> assertThat(moment.getComment()).isEqualTo(1));
      }
    }
  }
}
