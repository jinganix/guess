package io.github.jinganix.guess.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/** Host Modulith verification: scan domains only, assert no module cycles. */
@DisplayName("Host modulith")
class HostModulithTest {

  @Test
  @DisplayName("should verify host application modules")
  void shouldVerifyHostApplicationModules() {
    ApplicationModules.of("io.github.jinganix.guess.service.module").verify();
  }
}
