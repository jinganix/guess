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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuration for {@link WebMvcConfigurer}. */
@Configuration
@EnableConfigurationProperties(SpringDataWebProperties.class)
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Value("${utils.force-pageable:true}")
  private final boolean forcePageable;

  private final SpringDataWebProperties properties;

  @Bean
  SortHandlerMethodArgumentResolver defaultSortHandlerMethodArgumentResolver() {
    SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
    sortCustomizer().customize(sortResolver);
    return sortResolver;
  }

  @Bean
  @ConditionalOnMissingBean
  PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
    return (resolver) -> {
      SpringDataWebProperties.Pageable pageable = this.properties.getPageable();
      resolver.setPageParameterName(pageable.getPageParameter());
      resolver.setSizeParameterName(pageable.getSizeParameter());
      resolver.setOneIndexedParameters(pageable.isOneIndexedParameters());
      resolver.setPrefix(pageable.getPrefix());
      resolver.setQualifierDelimiter(pageable.getQualifierDelimiter());
      resolver.setFallbackPageable(PageRequest.of(0, pageable.getDefaultPageSize()));
      resolver.setMaxPageSize(pageable.getMaxPageSize());
    };
  }

  @Bean
  @ConditionalOnMissingBean
  SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
    return resolver -> resolver.setSortParameter(this.properties.getSort().getSortParameter());
  }

  /**
   * Add argument resolvers.
   *
   * @param resolvers list of {@link HandlerMethodArgumentResolver}.
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new UserIdArgumentResolver());
    resolvers.add(new WebpbHandlerMethodArgumentResolver());
    resolvers.add(defaultSortHandlerMethodArgumentResolver());
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
