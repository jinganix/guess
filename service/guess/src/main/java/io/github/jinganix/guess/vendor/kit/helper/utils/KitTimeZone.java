// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.helper.utils;

import java.time.ZoneId;

/**
 * Process-wide business time zone.
 *
 * <p>Kit has no default zone. It must be supplied through the {@link #PROPERTY} system property or
 * the {@link #ENV} environment variable; {@link #get()} throws when neither is configured. A silent
 * fallback (the old hardcoded {@code Asia/Shanghai}) hid JVM/host zone mismatches behind 8-hour
 * timestamp offsets, so an unconfigured process now fails on the first timestamp conversion instead.
 *
 * <p>Resolved once per JVM: run the service with {@code -Dkit.time-zone=UTC} (or
 * {@code KIT_TIME_ZONE=UTC}), matching the database session zone.
 */
public final class KitTimeZone {

  public static final String PROPERTY = "kit.time-zone";

  public static final String ENV = "KIT_TIME_ZONE";

  private static volatile ZoneId configured;

  private static volatile boolean resolved;

  private KitTimeZone() {}

  public static ZoneId get() {
    if (!resolved) {
      synchronized (KitTimeZone.class) {
        if (!resolved) {
          configured = parse();
          resolved = true;
        }
      }
    }
    ZoneId zone = configured;
    if (zone == null) {
      throw new IllegalStateException(
          "Kit time zone is not configured: set -D"
              + PROPERTY
              + "=<zone-id> or "
              + ENV
              + "=<zone-id> (for example UTC or Asia/Shanghai)");
    }
    return zone;
  }

  /** Test/ops override; {@code null} restores the configured zone. */
  public static void set(ZoneId zone) {
    synchronized (KitTimeZone.class) {
      configured = zone;
      resolved = zone != null;
    }
  }

  private static ZoneId parse() {
    return parse(System.getProperty(PROPERTY, System.getenv(ENV)));
  }

  static ZoneId parse(String raw) {
    return raw == null || raw.isBlank() ? null : ZoneId.of(raw.strip());
  }
}
