package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.helpers.FakeTimeSeries
import com.pschlup.ta.timeseries.UnstablePeriodException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class ExponentialMovingAverageTest {
  private val timeSeries = FakeTimeSeries.of(
    doubleArrayOf(6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0)
  )

  @Test
  fun `should compute average`() {
    val ema = timeSeries.closePrice.ema(10)

    assertThat(ema.valueAt(1)).isWithin(0.0001).of(3.61416)
    assertThat(ema.valueAt(0)).isWithin(0.0001).of(4.13886)
  }

  @Test
  fun `should throw if unstable`() {
    val ema = timeSeries.closePrice.ema(30)

    assertThrows(UnstablePeriodException::class.java) { ema.valueAt(0) }
  }
}
