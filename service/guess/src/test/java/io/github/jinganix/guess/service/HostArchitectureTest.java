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

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.github.jinganix.guess.vendor.kit.arch.KitArchitectureRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Host layering guard: scans the host root only (never {@code vendor.kit}). Rule SSOT is the
 * vendored {@link KitArchitectureRules} (buyout copy of kit {@code service/arch-rules}).
 *
 * <p>Mirrors {@code checkHostDefaults} rule by rule instead of calling the aggregate, so the
 * business-clock rule can use its scoped overload: the two remaining {@code
 * System.currentTimeMillis} callsites are the clocks themselves, not business time. {@code
 * Snowflake.getMillis} stays on the wall clock deliberately (mirrors kit: minted IDs are persisted
 * and ordered, a frozen business clock must not stall ID generation), and host {@code
 * UtilsService.currentTimeMillis} is the testable-clock seam all business code goes through (same
 * grandfathering as kit's own three callsites). Follow-up: route the host clock through the
 * vendored {@code TimeProvider} SPI instead.
 */
@DisplayName("HostArchitecture")
class HostArchitectureTest {

  private static final String HOST_ROOT = "io.github.jinganix.guess.service";

  /** Faces under test: {@code service.module.*} (domain modules). */
  private static final String[] PATTERNS = {"..module.."};

  private static final String[] ROOTS = {HOST_ROOT + ".module"};

  private static final JavaClasses HOST_CLASSES =
      new ClassFileImporter()
          .withImportOption(new ImportOption.DoNotIncludeTests())
          .importPackages(HOST_ROOT);

  @Test
  @DisplayName("should satisfy kit host defaults when organizing modules")
  void shouldSatisfyKitHostDefaultsWhenOrganizingModules() {
    KitArchitectureRules.checkControllersThin(HOST_CLASSES);
    KitArchitectureRules.checkHandlersFreeOfControllers(HOST_CLASSES);
    KitArchitectureRules.checkServicesFreeOfHandlerAndController(HOST_CLASSES);
    KitArchitectureRules.checkServiceImplsFreeOfHandlerAndController(HOST_CLASSES);
    KitArchitectureRules.checkServicesFreeOfProtoEnvelopes(HOST_CLASSES, PATTERNS);
    KitArchitectureRules.checkRepositoriesLocatedAndFree(HOST_CLASSES);
    KitArchitectureRules.checkHandlersLocated(HOST_CLASSES, PATTERNS);
    KitArchitectureRules.checkHandlersAsComponents(HOST_CLASSES);
    KitArchitectureRules.checkHandlersNotAsServices(HOST_CLASSES);
    KitArchitectureRules.checkHandlersTransactional(HOST_CLASSES);
    KitArchitectureRules.checkHandlersFreeOfHttpMappings(HOST_CLASSES);
    KitArchitectureRules.checkControllersOutOfInternal(HOST_CLASSES);
    KitArchitectureRules.checkHandlersOwnUseCaseLogic(HOST_CLASSES);
    KitArchitectureRules.checkNoConfigPackage(HOST_CLASSES, PATTERNS);
    KitArchitectureRules.checkServiceMigrationsLiveInMigrationsPackage(HOST_CLASSES);
    KitArchitectureRules.checkDomainRootFreeOfFourthKind(HOST_CLASSES, ROOTS);
    KitArchitectureRules.checkInjectionNaming(HOST_CLASSES);
    KitArchitectureRules.checkNoSystemCurrentTimeMillis(HOST_CLASSES, CLOCK_SCOPE);
    KitArchitectureRules.checkNoStringEnumFields(HOST_CLASSES);
    KitArchitectureRules.checkNoForbiddenPlatformCalls(HOST_CLASSES, PATTERNS);
    // checkMapperRules stays opt-in (progressive): the Pb-mapping part is clean after the
    // MomentMapper move, but entity-filling still reports 30 callsites across Like/Report /
    // Follow / Answer handlers — migrate handler by handler, then wire.
  }

  /**
   * Everything except the two clock sources (see class javadoc). Business time must still flow
   * through {@code UtilsService}, never {@code System} directly.
   */
  private static final DescribedPredicate<JavaClass> CLOCK_SCOPE =
      DescribedPredicate.describe(
          "business code (clock sources excluded)",
          clazz ->
              !clazz.getName().equals("io.github.jinganix.guess.service.helper.uid.Snowflake")
                  && !clazz
                      .getName()
                      .equals("io.github.jinganix.guess.service.helper.utils.UtilsService"));
}
