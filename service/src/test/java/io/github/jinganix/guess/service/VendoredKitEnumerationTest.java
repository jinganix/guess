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

import io.github.jinganix.guess.service.module.moment.model.MomentStatus;
import io.github.jinganix.guess.vendor.kit.helper.enumeration.EnumMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Vendored enumeration wiring (kit-adopt: host copies of {@code EnumMapper} / {@code
 * IntegerEnumMapper} deleted, host enums now resolve through {@code vendor.kit}).
 */
@DisplayName("VendoredKitEnumeration")
class VendoredKitEnumerationTest {

  @Test
  @DisplayName("then host enums resolve through the vendored mapper")
  void thenHostEnumsResolveThroughVendoredMapper() {
    assertThat(MomentStatus.fromValue(0)).isEqualTo(MomentStatus.ACTIVE);
    assertThat(MomentStatus.fromValue(1)).isEqualTo(MomentStatus.DELETED);
    assertThat(MomentStatus.fromValue(99)).isNull();
  }

  @Test
  @DisplayName("then the vendored mapper maps by key")
  void thenVendoredMapperMapsByKey() {
    EnumMapper<String, MomentStatus> mapper =
        new EnumMapper<>(MomentStatus.values(), MomentStatus::name);
    assertThat(mapper.fromValue("REPORTED")).isEqualTo(MomentStatus.REPORTED);
  }
}
