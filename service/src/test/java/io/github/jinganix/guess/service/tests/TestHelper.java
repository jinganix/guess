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

import io.github.jinganix.guess.service.helper.AbstractEntity;
import io.github.jinganix.guess.service.helper.auth.token.TokenService;
import io.github.jinganix.guess.service.module.auth.model.GrantedRole;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
@Service
public class TestHelper implements ApplicationContextAware {

  private final Map<String, CrudRepository<AbstractEntity, ?>> repositories = new HashMap<>();

  @Autowired private MockMvc mockMvc;

  @Autowired private TokenService tokenService;

  @Override
  @SuppressWarnings("unchecked")
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    context.getBeansOfType(CrudRepository.class).forEach(repositories::put);
  }

  public void clearAll() {
    for (CrudRepository<?, ?> repository : repositories.values()) {
      if (repository.count() > 0) {
        repository.deleteAll();
      }
    }
  }

  public void insertEntities(AbstractEntity... entities) {
    for (AbstractEntity entity : entities) {
      String beanName = StringUtils.uncapitalize(entity.getClass().getSimpleName()) + "Repository";
      CrudRepository<AbstractEntity, ?> repository = this.repositories.get(beanName);
      repository.save(entity);
    }
  }

  public ResultActions request(Long userId, WebpbMessage message) throws Exception {
    HttpMethod method = HttpMethod.valueOf(message.webpbMeta().getMethod());
    String token =
        tokenService.generate(
            userId,
            UUID.randomUUID().toString(),
            Arrays.stream(GrantedRole.values()).map(GrantedRole::getScope).toArray(String[]::new));
    return mockMvc.perform(
        MockMvcRequestBuilders.request(method, WebpbUtils.formatUrl(message))
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebpbUtils.serialize(message)));
  }

  public <T extends WebpbMessage> T deserialize(MvcResult result, Class<T> type)
      throws UnsupportedEncodingException {
    return WebpbUtils.deserialize(result.getResponse().getContentAsString(), type);
  }
}
