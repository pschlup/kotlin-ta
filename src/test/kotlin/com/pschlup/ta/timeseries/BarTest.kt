package com.pschlup.ta.timeseries

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class BarTest {
  @Test
  fun shouldIncludeTickTimestamp() {
    val openTime = Instant.parse("2019-04-19T20:00:00Z")
    val bar = Bar(TimeFrame.M5, openTime, 0.0)
    val tickTime = Instant.parse("2019-04-19T20:04:15Z")

    assertThat(bar.includesTimestamp(tickTime)).isTrue()
  }

  @Test
  fun shouldNotIncludeNextOpenTimestamp() {
    val openTime = Instant.parse("2019-04-19T20:00:00Z")
    val bar = Bar(TimeFrame.M5, openTime, 0.0)
    val nextOpen = Instant.parse("2019-04-19T20:05:00Z")

    assertThat(bar.includesTimestamp(nextOpen)).isFalse()
  }
}
