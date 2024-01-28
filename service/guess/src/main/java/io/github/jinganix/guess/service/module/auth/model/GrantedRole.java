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

package io.github.jinganix.guess.service.module.auth.model;

import io.github.jinganix.guess.service.helper.enumeration.EnumMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum GrantedRole {
  VISITOR("visitor"),
  USER("user");

  private static final EnumMapper<String, GrantedRole> mapper =
      new EnumMapper<>(values(), GrantedRole::getScope);

  private final String scope;

  private final GrantedAuthority authority;

  GrantedRole(String scope) {
    this.scope = scope;
    this.authority = new SimpleGrantedAuthority("ROLE_" + scope);
  }

  public static GrantedRole fromValue(String value) {
    return mapper.fromValue(value);
  }

  public String getScope() {
    return scope;
  }

  public GrantedAuthority getAuthority() {
    return authority;
  }
}
