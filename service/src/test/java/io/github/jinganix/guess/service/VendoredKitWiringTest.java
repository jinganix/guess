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

import io.github.jinganix.guess.service.tests.TestConst;
import io.github.jinganix.guess.vendor.kit.helper.utils.KitTimeZone;
import io.github.jinganix.guess.vendor.kit.helper.utils.UtilsService;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Vendored kit wiring (buyout pilot, kit c32c22eb4ddd).
 *
 * <p>Guards the failure mode where the vendored {@code helper.utils} {@code @Component} silently
 * drops out of the Spring scan after a package rewrite: the context would still boot and only fail
 * at injection time. This test boots a minimal context over the vendored package and asserts the
 * {@code UtilsService} bean loads.
 *
 * <p>Host clock choice: the process runs with {@code -Dkit.time-zone=UTC} (or {@code
 * KIT_TIME_ZONE=UTC}), matching the database session zone; there is no kit default and {@link
 * KitTimeZone#get()} throws when unconfigured. Tests freeze business time by contributing a {@code
 * TimeProvider} bean (as below) instead of subclassing the component. Freezing is tests-only:
 * payment and object-storage signatures are verified against the real wall-clock, so a frozen
 * provider in production breaks them. The zone itself is not part of this SPI: it is global process
 * configuration.
 */
@DisplayName("VendoredKitWiring")
class VendoredKitWiringTest {

  @Test
  @DisplayName("then GuessApplication scans the vendored kit package")
  void thenScanBasePackagesCoverVendorKit() {
    SpringBootApplication annotation =
        GuessApplication.class.getAnnotation(SpringBootApplication.class);
    assertThat(annotation).isNotNull();
    assertThat(annotation.scanBasePackages())
        .contains("io.github.jinganix.guess.service", "io.github.jinganix.guess.vendor.kit");
  }

  @Test
  @DisplayName("then vendored UtilsService loads and honours the TimeProvider")
  void thenVendoredUtilsServiceLoads() {
    KitTimeZone.set(ZoneId.of("UTC"));
    try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
      context.register(TestConfig.class);
      context.refresh();
      UtilsService service = context.getBean(UtilsService.class);
      assertThat(service).isNotNull();
      assertThat(service.currentTimeMillis()).isEqualTo(TestConst.MILLIS);
      assertThat(service.zoneId()).isEqualTo(ZoneId.of("UTC"));
    } finally {
      KitTimeZone.set(null);
    }
  }

  @Configuration
  @ComponentScan(basePackages = "io.github.jinganix.guess.vendor.kit")
  static class TestConfig {

    @Bean
    UtilsService.TimeProvider timeProvider() {
      return () -> TestConst.MILLIS;
    }
  }
}
