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

package io.github.jinganix.guess.service.setup.config;

import io.github.jinganix.guess.service.helper.AuthedUser;
import io.github.jinganix.guess.service.setup.argument.mvc.UserIdArgumentResolver;
import io.github.jinganix.webpb.runtime.mvc.WebpbHandlerMethodArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuration for {@link WebMvcConfigurer}. */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  /**
   * Add argument resolvers.
   *
   * @param resolvers list of {@link HandlerMethodArgumentResolver}.
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new UserIdArgumentResolver());
    resolvers.add(new WebpbHandlerMethodArgumentResolver());
    resolvers.add(
        new HandlerMethodArgumentResolver() {
          @Override
          public boolean supportsParameter(MethodParameter parameter) {
            return AuthedUser.class.isAssignableFrom(parameter.getParameterType());
          }

          @Override
          public Object resolveArgument(
              MethodParameter parameter,
              ModelAndViewContainer mavContainer,
              NativeWebRequest webRequest,
              WebDataBinderFactory binderFactory) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            return new AuthedUser().setId(userId);
          }
        });
  }
}
