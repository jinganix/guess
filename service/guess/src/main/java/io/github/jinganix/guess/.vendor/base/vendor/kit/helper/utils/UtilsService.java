// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.helper.utils;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UtilsService {

  /**
   * Host SPI for the business clock. When no bean is provided the service falls back to {@link
   * System#currentTimeMillis()}.
   *
   * <p>Hosts freeze business time in tests by contributing a {@code TimeProvider} bean (for example
   * one backed by a request-scoped context cache) instead of subclassing this component. Freeze
   * in tests only: external signatures (payment, object-storage policies) are validated against
   * real wall-clock, so a frozen provider in production breaks them. The zone is
   * not part of this SPI: it is global process configuration, see {@link KitTimeZone}.
   */
  public interface TimeProvider {

    long currentTimeMillis();
  }

  public static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

  public static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static final DateTimeFormatter DATE_NUMBER_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  public static final long DAY_MILLIS = TimeUnit.DAYS.toMillis(1);

  public static final long WEEK_MILLIS = TimeUnit.DAYS.toMillis(7);

  public static final long HOUR_MILLIS = TimeUnit.HOURS.toMillis(1);

  @Autowired(required = false)
  TimeProvider timeProvider;

  public long currentTimeMillis() {
    TimeProvider provider = this.timeProvider;
    return provider == null ? System.currentTimeMillis() : provider.currentTimeMillis();
  }

  /**
   * Clock backed by {@link #currentTimeMillis()}. Verifiers must build on this rather than on the
   * system clock: issuance and verification sharing one clock keeps frozen test clocks (and ops
   * clock skew) from rejecting freshly issued tokens.
   */
  public Clock clock() {
    return new Clock() {
      @Override
      public ZoneId getZone() {
        return ZoneOffset.UTC;
      }

      @Override
      public Clock withZone(ZoneId zone) {
        return this;
      }

      @Override
      public Instant instant() {
        return Instant.ofEpochMilli(currentTimeMillis());
      }
    };
  }

  /**
   * Monotonic clock for short-lived in-memory expiries (e.g. Caffeine variable expiry).
   *
   * <p>Unlike {@link #currentTimeMillis()} this is not business time: it cannot be frozen by a
   * {@link TimeProvider} and must never be persisted or compared against wall-clock values.
   */
  public long nanoTime() {
    return System.nanoTime();
  }

  /**
   * Configured business zone. Kit keeps no zone default; {@link KitTimeZone#get()} throws when
   * {@code kit.time-zone} is not configured.
   */
  public ZoneId zoneId() {
    return KitTimeZone.get();
  }

  public boolean isToday(long millis) {
    return toLocalDate(currentTimeMillis()).equals(toLocalDate(millis));
  }

  public boolean isToday(Long millis) {
    return millis != null && isToday(millis.longValue());
  }

  public LocalDate nowDate() {
    return LocalDate.now(zoneId());
  }

  public LocalDateTime nowDatetime() {
    return LocalDateTime.now(zoneId());
  }

  public LocalDate toLocalDate(long millis) {
    return Instant.ofEpochMilli(millis).atZone(zoneId()).toLocalDate();
  }

  public LocalDateTime toLocalDateTime(Long millis) {
    return millis == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zoneId());
  }

  public boolean isSameDay(long a, long b) {
    return toLocalDate(a).equals(toLocalDate(b));
  }

  public boolean isSameDay(Long a, long b) {
    return a != null && isSameDay(a.longValue(), b);
  }

  public boolean isSameDay(long a, Long b) {
    return b != null && isSameDay(a, b.longValue());
  }

  public boolean isThisWeek(long millis) {
    return toLocalDate(millis)
        .with(WeekFields.ISO.dayOfWeek(), 1)
        .equals(toLocalDate(currentTimeMillis()).with(WeekFields.ISO.dayOfWeek(), 1));
  }

  public boolean isThisMonth(long millis) {
    return toLocalDate(currentTimeMillis())
        .withDayOfMonth(1)
        .equals(toLocalDate(millis).withDayOfMonth(1));
  }

  public <T> T getOneOrLast(List<T> values, int index) {
    if (values == null || values.isEmpty()) {
      throw new IllegalArgumentException("values must not be null or empty");
    }
    if (index < 0) {
      throw new IllegalArgumentException("index must not be negative: " + index);
    }
    return index >= values.size() ? values.get(values.size() - 1) : values.get(index);
  }

  public int getDays(long time) {
    return (int) toLocalDate(time).toEpochDay();
  }

  public int getDiffDays(long millisA, long millisB) {
    return Math.abs(getDays(millisA) - getDays(millisB));
  }

  public long dateNumber(LocalDate date) {
    return Long.parseLong(DATE_NUMBER_FMT.format(date));
  }

  public boolean isSameWeek(Long a, long b) {
    return a != null && getWeeks(a) == getWeeks(b);
  }

  public boolean isSameWeek(long a, Long b) {
    return b != null && getWeeks(a) == getWeeks(b);
  }

  public boolean isSameWeek(long a, long b) {
    return getWeeks(a) == getWeeks(b);
  }

  private long getWeeks(long millis) {
    // Week index anchored three days before the configured-zone day index (no fixed offset).
    return Math.floorDiv(getDays(millis) + 3L, 7L);
  }

  public boolean isSameMonth(Long a, long b) {
    return a != null && toLocalDate(a).withDayOfMonth(1).equals(toLocalDate(b).withDayOfMonth(1));
  }

  public boolean isSameMonth(long a, Long b) {
    return b != null && toLocalDate(a).withDayOfMonth(1).equals(toLocalDate(b).withDayOfMonth(1));
  }

  public boolean isSameMonth(long timeA, long timeB) {
    return toLocalDate(timeA).withDayOfMonth(1).equals(toLocalDate(timeB).withDayOfMonth(1));
  }

  public int daysBetween(LocalDate dateA, LocalDate dateB) {
    return (int) DAYS.between(dateA, dateB);
  }

  public long toMillis(LocalDateTime dateTime) {
    return dateTime.atZone(zoneId()).toInstant().toEpochMilli();
  }

  public long toMillis(LocalDate date) {
    return date.atStartOfDay(zoneId()).toInstant().toEpochMilli();
  }

  public String format(LocalDateTime dateTime) {
    return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
  }

  public String format(Long millis) {
    return format(toLocalDateTime(millis));
  }

  public long startOfHour(long millis) {
    // Local calendar hour, not a UTC hour block: a half-hour offset (IST +5:30, NPT +5:45)
    // would otherwise start the window at :30 past the local hour.
    return toMillis(toLocalDateTime(millis).truncatedTo(java.time.temporal.ChronoUnit.HOURS));
  }

  public long endOfHour(long millis) {
    return toMillis(
            toLocalDateTime(millis).truncatedTo(java.time.temporal.ChronoUnit.HOURS).plusHours(1))
        - 1;
  }

  // Boundaries are local calendar days, not flat 24h/168h blocks: on a DST day the offset
  // changes inside the block, so the arithmetic form lands an hour off twice a year.
  public long startOfDay(long millis) {
    return toMillis(toLocalDate(millis));
  }

  public long endOfDay(long millis) {
    return toMillis(toLocalDate(millis).plusDays(1)) - 1;
  }

  public long startOfWeek(long millis) {
    return toMillis(toLocalDate(millis).with(WeekFields.ISO.dayOfWeek(), 1));
  }

  public long endOfWeek(long millis) {
    return toMillis(toLocalDate(millis).with(WeekFields.ISO.dayOfWeek(), 1).plusWeeks(1)) - 1;
  }

  public long startOfMonth(long millis) {
    return toMillis(toLocalDate(millis).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
  }

  public long endOfMonth(long millis) {
    return toMillis(
            toLocalDate(millis)
                .with(TemporalAdjusters.lastDayOfMonth())
                .plusDays(1)
                .atStartOfDay())
        - 1;
  }

  public long startOfYear(long millis) {
    return toMillis(toLocalDate(millis).with(TemporalAdjusters.firstDayOfYear()).atStartOfDay());
  }

  public long endOfYear(long millis) {
    return toMillis(
            toLocalDate(millis)
                .with(TemporalAdjusters.lastDayOfYear())
                .plusDays(1)
                .atStartOfDay())
        - 1;
  }

  public <T> boolean notEquals(Collection<T> a, Collection<T> b) {
    if (a == null || b == null) {
      return a != b;
    }
    return !new HashSet<>(a).equals(new HashSet<>(b));
  }

  /**
   * Generate uuid.
   *
   * @param dash true with dash
   * @return uuid
   */
  public String uuid(boolean dash) {
    if (dash) {
      return UUID.randomUUID().toString();
    } else {
      return UUID.randomUUID().toString().replace("-", "");
    }
  }

  public boolean isEmail(String str) {
    return str != null && EMAIL_PATTERN.matcher(str).matches();
  }

  public boolean isFullwidthCodePoint(int codePoint) {
    return (codePoint >= 0x1100
        && (codePoint <= 0x115f
            || codePoint == 0x2329
            || codePoint == 0x232a
            || (0x2e80 <= codePoint && codePoint <= 0x3247 && codePoint != 0x303f)
            || (0x3250 <= codePoint && codePoint <= 0x4dbf)
            || (0x4e00 <= codePoint && codePoint <= 0xa4c6)
            || (0xa960 <= codePoint && codePoint <= 0xa97c)
            || (0xac00 <= codePoint && codePoint <= 0xd7a3)
            || (0xf900 <= codePoint && codePoint <= 0xfaff)
            || (0xfe10 <= codePoint && codePoint <= 0xfe19)
            || (0xfe30 <= codePoint && codePoint <= 0xfe6b)
            || (0xff01 <= codePoint && codePoint <= 0xff60)
            || (0xffe0 <= codePoint && codePoint <= 0xffe6)
            || (0x1b000 <= codePoint && codePoint <= 0x1b001)
            || (0x1f200 <= codePoint && codePoint <= 0x1f251)
            || (0x20000 <= codePoint && codePoint <= 0x3fffd)));
  }

  public int stringWidth(String str) {
    int width = 0;
    int index = 0;
    while (index < str.length()) {
      // Advance by the whole code point: an astral full-width char is one wide cell, not
      // a wide high surrogate plus a narrow low surrogate.
      int codePoint = str.codePointAt(index);
      width += isFullwidthCodePoint(codePoint) ? 2 : 1;
      index += Character.charCount(codePoint);
    }
    return width;
  }
}
