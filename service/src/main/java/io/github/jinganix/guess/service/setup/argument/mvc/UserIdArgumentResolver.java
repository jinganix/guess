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

package io.github.jinganix.guess.service.setup.argument.mvc;

import io.github.jinganix.guess.service.helper.auth.token.UserAuthenticationToken;
import io.github.jinganix.guess.service.setup.argument.annotations.UserId;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return findMethodAnnotation(UserId.class, parameter) != null;
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
    if (request == null) {
      throw new IllegalStateException(
          "Current request is not of type HttpServletRequest: " + webRequest);
    }

    UserAuthenticationToken authentication = (UserAuthenticationToken) request.getUserPrincipal();
    return authentication == null ? null : authentication.getPrincipal();
  }

  private <T extends Annotation> T findMethodAnnotation(
      Class<T> annotationClass, MethodParameter parameter) {
    T annotation = parameter.getParameterAnnotation(annotationClass);
    if (annotation != null) {
      return annotation;
    }
    Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
    for (Annotation toSearch : annotationsToSearch) {
      annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }
}
