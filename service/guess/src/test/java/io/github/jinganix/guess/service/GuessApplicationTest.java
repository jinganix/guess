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

package io.github.jinganix.guess.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mockStatic;

import io.github.jinganix.guess.service.tests.SpringBootIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("GuessApplication")
class GuessApplicationTest extends SpringBootIntegrationTests {

  @Nested
  @DisplayName("when load context")
  class WhenLoadContext {

    @Test
    @DisplayName("then loaded")
    void thenLoaded() {
      assertThatCode(GuessApplicationTest::new).doesNotThrowAnyException();
      try (MockedStatic<SpringApplication> application = mockStatic(SpringApplication.class)) {
        assertThat(application).isNotNull();
        assertThatCode(() -> GuessApplication.main(null)).doesNotThrowAnyException();
      }
    }
  }
}
