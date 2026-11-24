// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.helper.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public final class MonthRangeUtils {

  private MonthRangeUtils() {}

  public static int daysInMonth(YearMonth month) {
    return month.lengthOfMonth();
  }

  public static LocalDate monthStart(YearMonth month) {
    return month.atDay(1);
  }

  public static LocalDate monthEnd(YearMonth month) {
    return month.atEndOfMonth();
  }

  /** Inclusive overlap days between [start, end] and a calendar month. */
  public static int overlapDays(LocalDate rangeStart, LocalDate rangeEnd, YearMonth month) {
    LocalDate monthStart = monthStart(month);
    LocalDate monthEnd = monthEnd(month);
    LocalDate effectiveStart = rangeStart.isAfter(monthStart) ? rangeStart : monthStart;
    LocalDate effectiveEnd = rangeEnd.isBefore(monthEnd) ? rangeEnd : monthEnd;
    if (effectiveStart.isAfter(effectiveEnd)) {
      return 0;
    }
    return (int) ChronoUnit.DAYS.between(effectiveStart, effectiveEnd) + 1;
  }

  /**
   * Stay-day overlap for [start, end). When {@code rangeEnd} is null, treats end as inclusive month
   * end (still in residence). When set (check-out date), the check-out day is not charged.
   */
  public static int overlapDaysOpenEnd(LocalDate rangeStart, LocalDate rangeEnd, YearMonth month) {
    if (rangeEnd == null) {
      return overlapDays(rangeStart, monthEnd(month), month);
    }
    LocalDate lastChargedDay = rangeEnd.minusDays(1);
    if (lastChargedDay.isBefore(rangeStart)) {
      return 0;
    }
    return overlapDays(rangeStart, lastChargedDay, month);
  }
}
