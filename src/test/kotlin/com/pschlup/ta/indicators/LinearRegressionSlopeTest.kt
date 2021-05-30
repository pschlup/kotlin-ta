package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.helpers.FakeTimeSeries
import com.pschlup.ta.timeseries.UnstablePeriodException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class LinearRegressionSlopeTest {
  @Test
  fun `should compute slope of1`() {
    val source: Indicator = FakeTimeSeries.of(doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)).closePrice
    val slope = source.linearRegressionSlope(10)

    assertThat(slope.valueAt(0)).isEqualTo(1.0)
  }

  @Test
  fun `should compute slope of2`() {
    val source: Indicator = FakeTimeSeries.of(doubleArrayOf(0.0, 2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0)).closePrice
    val slope = source.linearRegressionSlope(10)

    assertThat(slope.valueAt(0)).isEqualTo(2.0)
  }

  @Test
  fun `should compute slope of half`() {
    val source: Indicator = FakeTimeSeries.of(doubleArrayOf(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0)).closePrice
    val slope = source.linearRegressionSlope(8)

    assertThat(slope.valueAt(0)).isEqualTo(.5)
  }

  @Test
  fun `should compute slope of zero`() {
    val source: Indicator = FakeTimeSeries.of(doubleArrayOf(1.0, 2.0, 3.0, 2.0, 1.0, 2.0, 3.0, 2.0, 1.0)).closePrice
    val slope = source.linearRegressionSlope(9)

    assertThat(slope.valueAt(0)).isEqualTo(0.0)
  }

  @Test
  fun `should throw if unstable`() {
    val source: Indicator = FakeTimeSeries.of(doubleArrayOf(1.0, 2.0, 3.0)).closePrice
    val slope = source.linearRegressionSlope(20)

    assertThrows(UnstablePeriodException::class.java) { slope.valueAt(0) }
  }
}
