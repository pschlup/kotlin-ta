package com.pschlup.ta.timeseries

import java.time.Duration

enum class TimeFrame(val duration: Duration) {
  M5(Duration.ofMinutes(5)),
  M10(Duration.ofMinutes(10)),
  M15(Duration.ofMinutes(15)),
  M30(Duration.ofMinutes(30)),
  H1(Duration.ofHours(1)),
  H2(Duration.ofHours(2)),
  H4(Duration.ofHours(4)),
  D(Duration.ofDays(1));

  companion object {
    @JvmStatic
    fun of(duration: Duration): TimeFrame = when (duration.toMinutes().toInt()) {
      5 -> M5
      10 -> M10
      15 -> M15
      30 -> M30
      60 -> H1
      120 -> H2
      240 -> H4
      1440 -> D
      else -> throw IllegalArgumentException("Unknown timeframe of ${duration.toMinutes()} minutes")
    }
  }
}