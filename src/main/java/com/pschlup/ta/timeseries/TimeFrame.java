package com.pschlup.ta.timeseries;

import java.time.Duration;

@SuppressWarnings("unused")
public enum TimeFrame {

  M5(Duration.ofMinutes(5)),
  M10(Duration.ofMinutes(10)),
  M15(Duration.ofMinutes(15)),
  M30(Duration.ofMinutes(30)),
  H1(Duration.ofHours(1)),
  H2(Duration.ofHours(2)),
  H4(Duration.ofHours(4)),
  D(Duration.ofDays(1));

  private final Duration duration;

  TimeFrame(Duration duration) {
    this.duration = duration;
  }

  public Duration duration() {
    return this.duration;
  }

  public static TimeFrame of(Duration duration) {
    switch ((int)duration.toMinutes()) {
      case 5:
        return M5;
      case 10:
        return M10;
      case 15:
        return M15;
      case 30:
        return M30;
      case 60:
        return H1;
      case 120:
        return H2;
      case 240:
        return H4;
      case 1440:
        return D;
      default:
        throw new IllegalArgumentException(
            String.format("Unknown timeframe of %d minutes", duration.toMinutes()));
    }
  }
}
