package com.pschlup.ta.timeseries;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.google.common.truth.Truth.assertThat;

class BarTest {

  @Test
  void shouldIncludeTickTimestamp() {
    Instant openTime = Instant.parse("2019-04-19T20:00:00Z");
    Bar bar = new Bar(TimeFrame.M5, openTime, 0.);
    Instant tickTime = Instant.parse("2019-04-19T20:04:15Z");
    assertThat(bar.includesTimestamp(tickTime)).isTrue();
  }

  @Test
  void shouldNotIncludeNextOpenTimestamp() {
    Instant openTime = Instant.parse("2019-04-19T20:00:00Z");
    Bar bar = new Bar(TimeFrame.M5, openTime, 0.);
    Instant nextOpen = Instant.parse("2019-04-19T20:05:00Z");
    assertThat(bar.includesTimestamp(nextOpen)).isFalse();
  }
}
