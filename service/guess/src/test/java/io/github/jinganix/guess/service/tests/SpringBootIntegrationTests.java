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

import static io.github.jinganix.guess.service.tests.TestConst.MILLIS;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import io.github.jinganix.guess.service.helper.auth.token.TokenService;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.guess.service.helper.utils.UtilsService;
import io.github.jinganix.guess.service.module.auth.authenticator.WeappCodeAuthenticator;
import io.github.jinganix.guess.service.module.wechat.WeappProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** Tests for spring mvc. */
@ContextConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith({MysqlExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class SpringBootIntegrationTests {

  @MockitoSpyBean protected TokenService tokenService;

  @MockitoSpyBean protected UidGenerator uidGenerator;

  @MockitoSpyBean protected UtilsService utilsService;

  @MockitoSpyBean protected WeappCodeAuthenticator weappCodeAuthenticator;

  @MockitoSpyBean protected WeappProvider weappProvider;

  @BeforeEach
  protected void commonSetup() {
    when(utilsService.currentTimeMillis()).thenReturn(MILLIS);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
      return MockMvcBuilders.webAppContextSetup(webApplicationContext)
          .apply(springSecurity())
          .build();
    }
  }
}
